/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.jpa.repositories.BookRepository;
import com.kirjaswappi.backend.jpa.repositories.GenreRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.BookMapper;
import com.kirjaswappi.backend.mapper.ExchangeConditionMapper;
import com.kirjaswappi.backend.mapper.GenreMapper;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.exceptions.BookNotFoundException;
import com.kirjaswappi.backend.service.exceptions.GenreNotFoundException;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;
import com.kirjaswappi.backend.service.filters.FindAllBooksFilter;

@Service
@Transactional
public class BookService {
  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private GenreRepository genreRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PhotoService photoService;

  private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("title", "author", "language", "condition",
      "genres.name");

  public Book createBook(Book book) {
    setValidExchangeableGenresIfExists(book);
    var bookDao = BookMapper.toDao(book);
    addGenresToBook(book, bookDao);
    setOwnerToBook(book, bookDao);
    var savedDao = bookRepository.save(bookDao);
    savedDao = addCoverPhotoToBook(book, savedDao);
    addCoverPhotoToExchangeableBooksIfExists(book, savedDao);
    addBookToOwner(savedDao);
    return bookWithImageUrlAndOwner(savedDao);
  }

  private void addCoverPhotoToExchangeableBooksIfExists(Book parentBook, BookDao bookDao) {
    var exchangeableBooks = parentBook.getExchangeCondition().getExchangeableBooks();
    if (exchangeableBooks == null || exchangeableBooks.isEmpty()) {
      return;
    }
    for (int i = 0; i < exchangeableBooks.size(); i++) {
      var coverPhoto = exchangeableBooks.get(i).getCoverPhotoFile();
      var exchangeableBookId = bookDao.getExchangeCondition().getExchangeableBooks().get(i).getId();
      String uniqueId = photoService.addBookCoverPhoto(coverPhoto, exchangeableBookId);
      bookDao.getExchangeCondition().getExchangeableBooks().get(i).setCoverPhoto(uniqueId);
    }
    bookRepository.save(bookDao);
  }

  public Book updateBook(Book book) {
    var dao = bookRepository.findById(book.getId())
        .orElseThrow(() -> new BookNotFoundException(book.getId()));
    updateDaoWithNewProperties(book, dao);
    var updatedBookDao = bookRepository.save(dao);
    updatedBookDao = addCoverPhotoToBook(book, updatedBookDao);
    return bookWithImageUrlAndOwner(updatedBookDao);
  }

  public Book getBookById(String id) {
    var bookDao = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
    return bookWithImageUrlAndOwner(bookDao);
  }

  public Page<Book> getAllBooksByFilter(FindAllBooksFilter filter, Pageable pageable) {
    var criteria = filter.buildSearchAndFilterCriteria();
    pageable = getPageableWithValidSortingCriteria(pageable);
    var bookDaos = bookRepository.findAllBooksByFilter(criteria, pageable);
    return mapToBookPage(bookDaos, pageable);
  }

  public void deleteBook(String id) {
    var dao = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    deleteExistingCoverPhoto(dao);
    removeBookFromOwner(dao);
    bookRepository.deleteById(id);
  }

  private void updateDaoWithNewProperties(Book book, BookDao dao) {
    dao.setTitle(book.getTitle());
    dao.setAuthor(book.getAuthor());
    dao.setDescription(book.getDescription());
    dao.setLanguage(book.getLanguage().name());
    dao.setCondition(book.getCondition().name());
    addGenresToBook(book, dao);
    setValidExchangeableGenresIfExists(book);
    dao.setExchangeCondition(ExchangeConditionMapper.toDao(book.getExchangeCondition()));
  }

  private void setValidExchangeableGenresIfExists(Book book) {
    var exchangeableGenres = book.getExchangeCondition().getExchangeableGenres();
    if (exchangeableGenres == null || exchangeableGenres.isEmpty()) {
      return;
    }
    var validGenres = exchangeableGenres.stream()
        .map(genre -> genreRepository.findByName(genre.getName())
            .orElseThrow(() -> new GenreNotFoundException(genre.getName())))
        .toList();
    book.getExchangeCondition().setExchangeableGenres(validGenres.stream().map(GenreMapper::toEntity).toList());
  }

  private void addGenresToBook(Book book, BookDao dao) {
    dao.setGenres(book.getGenres().stream()
        .map(genre -> genreRepository.findByName(genre.getName())
            .orElseThrow(() -> new GenreNotFoundException(genre.getName())))
        .toList());
  }

  private void setOwnerToBook(Book book, BookDao bookDao) {
    var owner = userRepository.findById(book.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(book.getOwner().getId()));
    bookDao.setOwner(owner);
  }

  private BookDao addCoverPhotoToBook(Book book, BookDao dao) {
    String uniqueId = photoService.addBookCoverPhoto(book.getCoverPhotoFile(), dao.getId());
    dao.setCoverPhoto(uniqueId);
    return bookRepository.save(dao);
  }

  private void addBookToOwner(BookDao dao) {
    var owner = userRepository.findById(dao.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(dao.getOwner().getId()));
    owner.setBooks(Optional.ofNullable(owner.getBooks()).orElseGet(ArrayList::new));
    owner.getBooks().add(dao);
    userRepository.save(owner);
  }

  private void deleteExistingCoverPhoto(BookDao dao) {
    if (dao.getCoverPhoto() != null) {
      photoService.deleteBookCoverPhoto(dao.getCoverPhoto());
    }
  }

  private void removeBookFromOwner(BookDao dao) {
    var owner = userRepository.findById(dao.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(dao.getOwner().getId()));
    if (owner.getBooks() != null) {
      owner.setBooks(owner.getBooks().stream()
          .filter(book -> !book.getId().equals(dao.getId()))
          .toList());
      userRepository.save(owner);
    }
  }

  private Pageable getPageableWithValidSortingCriteria(Pageable pageable) {
    if (!pageable.getSort().isSorted()) {
      return pageable;
    }

    List<Sort.Order> allowedOrders = pageable.getSort().stream()
        .filter(order -> ALLOWED_SORT_FIELDS.contains(order.getProperty()))
        .toList();

    if (allowedOrders.isEmpty()) {
      // remove sorting if no valid sorting criteria is found
      return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(allowedOrders));
  }

  private Book bookWithImageUrlAndOwner(BookDao bookDao) {
    var book = fetchImageUrlForBookCoverPhoto(bookDao);
    fetchImageUrlForExchangeableBooksIfExists(book);
    return bookWithOwner(bookDao, book);
  }

  @NotNull
  private static Book bookWithOwner(BookDao bookDao, Book book) {
    return BookMapper.setOwner(bookDao.getOwner(), book);
  }

  @NotNull
  private Book fetchImageUrlForBookCoverPhoto(BookDao bookDao) {
    var bookCoverPhotoImageUrl = photoService.getBookCoverPhoto(bookDao.getCoverPhoto());
    return BookMapper.toEntity(bookDao, bookCoverPhotoImageUrl);
  }

  private void fetchImageUrlForExchangeableBooksIfExists(Book parentBook) {
    var exchangeableBooks = parentBook.getExchangeCondition().getExchangeableBooks();
    if (exchangeableBooks == null || exchangeableBooks.isEmpty()) {
      return;
    }
    for (int i = 0; i < exchangeableBooks.size(); i++) {
      var uniqueId = exchangeableBooks.get(i).getCoverPhoto();
      String coverPhotoUrl = photoService.getBookCoverPhoto(uniqueId);
      parentBook.getExchangeCondition().getExchangeableBooks().get(i).setCoverPhoto(coverPhotoUrl);
    }
  }

  private Page<Book> mapToBookPage(Page<BookDao> bookDaos, Pageable pageable) {
    var books = bookDaos.stream().map(this::bookWithImageUrlAndOwner).toList();
    return new PageImpl<>(books, pageable, bookDaos.getTotalElements());
  }

  public void deleteAllBooks() {
    var bookDaos = bookRepository.findAll();
    for (var bookDao : bookDaos) {
      deleteBook(bookDao.getId());
    }
  }
}