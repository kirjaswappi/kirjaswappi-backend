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
import com.kirjaswappi.backend.mapper.ExchangeableBookMapper;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.ExchangeableBook;
import com.kirjaswappi.backend.service.exceptions.BookNotFoundException;
import com.kirjaswappi.backend.service.exceptions.GenreNotFoundException;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;
import com.kirjaswappi.backend.service.filters.GetAllBooksFilter;

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
    var bookDao = BookMapper.toDao(book);
    addGenresToBook(book, bookDao);
    setOwnerToBook(book, bookDao);
    var savedDao = bookRepository.save(bookDao);
    savedDao = addCoverPhotoToBook(book, savedDao);
    var exchangeBooks = book.getExchangeCondition().getExchangeableBooks();
    addCoverPhotoToExchangeBooks(exchangeBooks, savedDao);
    addBookToOwner(savedDao);
    return bookWithImageUrlAndOwner(savedDao);
  }

  private void addCoverPhotoToExchangeBooks(List<ExchangeableBook> exchangeableBooks, BookDao savedDao) {
    for (int i = 0; i < exchangeableBooks.size(); i++) {
      var exchangeableBook = exchangeableBooks.get(i);
      var exchangeableBookId = savedDao.getExchangeCondition().getExchangeableBooks().get(i).getId();
      String uniqueId = photoService.addBookCoverPhoto(exchangeableBook.getCoverPhotoFile(), exchangeableBookId);
      savedDao.getExchangeCondition().getExchangeableBooks().get(i).setCoverPhoto(uniqueId);
    }
    bookRepository.save(savedDao);
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

  public Page<Book> getAllBooksByFilter(GetAllBooksFilter filter, Pageable pageable) {
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
    dao.setExchangeCondition(ExchangeConditionMapper.toDao(book.getExchangeCondition()));
    addGenresToBook(book, dao);
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
    fetchImageUrlForExchangeableBooks(bookDao);
    var book = fetchImageUrlForBookCoverPhoto(bookDao);
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

  private void fetchImageUrlForExchangeableBooks(BookDao bookDao) {
    var exchangeCondition = bookDao.getExchangeCondition();
    if (exchangeCondition != null) {
      var exchangeableBooks = exchangeCondition.getExchangeableBooks();
      if (exchangeableBooks != null && !exchangeableBooks.isEmpty()) {
        var exchangeableBooksWithImageUrl = exchangeableBooks.stream()
            .map(exchangeableBook -> ExchangeableBookMapper.toDao(exchangeableBook,
                photoService.getBookCoverPhoto(exchangeableBook.getCoverPhoto())))
            .toList();
        exchangeCondition.setExchangeableBooks(exchangeableBooksWithImageUrl);
      }
    }
  }

  private Page<Book> mapToBookPage(Page<BookDao> bookDaos, Pageable pageable) {
    var books = bookDaos.stream().map(this::bookWithImageUrlAndOwner).toList();
    return new PageImpl<>(books, pageable, bookDaos.getTotalElements());
  }
}