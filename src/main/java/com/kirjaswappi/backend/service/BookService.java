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
import com.kirjaswappi.backend.mapper.GenreMapper;
import com.kirjaswappi.backend.mapper.SwapConditionMapper;
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
    setValidSwappableGenresIfExists(book);
    var bookDao = BookMapper.toDao(book);
    addGenresToBook(book, bookDao);
    setOwnerToBook(book, bookDao);
    var savedDao = bookRepository.save(bookDao);
    savedDao = addCoverPhotoToBook(book, savedDao);
    addCoverPhotoToSwappableBooksIfExists(book, savedDao);
    addBookToOwner(savedDao);
    return bookWithImageUrlAndOwner(savedDao);
  }

  public Book updateBook(Book updatedBook) {
    var existingBookDao = bookRepository.findByIdAndIsDeletedFalse(updatedBook.getId())
        .orElseThrow(() -> new BookNotFoundException(updatedBook.getId()));
    deleteExistingSwappableBooksCoverPhotoIfExists(existingBookDao);
    updateExistingDaoWithNewProperties(updatedBook, existingBookDao);
    var updatedBookDao = bookRepository.save(existingBookDao);
    updatedBookDao = updateBookCoverPhoto(updatedBook, updatedBookDao);
    addCoverPhotoToSwappableBooksIfExists(updatedBook, updatedBookDao);
    return bookWithImageUrlAndOwner(updatedBookDao);
  }

  private void deleteExistingSwappableBooksCoverPhotoIfExists(BookDao existingBookDao) {
    if (existingBookDao.getSwapCondition().getSwappableBooks() != null) {
      existingBookDao.getSwapCondition().getSwappableBooks().forEach(
          book -> photoService.deleteBookCoverPhoto(book.getCoverPhoto()));
    }
  }

  public Book getBookById(String id) {
    var bookDao = bookRepository.findByIdAndIsDeletedFalse(id)
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
    var bookDao = bookRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new BookNotFoundException(id));
    deleteExistingCoverPhoto(bookDao);
    removeBookFromOwner(bookDao);
    bookRepository.deleteLogically(id);
  }

  public void deleteAllBooks() {
    var bookDaos = bookRepository.findAllByIsDeletedFalse();
    for (var bookDao : bookDaos) {
      deleteBook(bookDao.getId());
    }
  }

  private void addCoverPhotoToSwappableBooksIfExists(Book parentBook, BookDao bookDao) {
    var swappableBooks = parentBook.getSwapCondition().getSwappableBooks();
    if (swappableBooks == null || swappableBooks.isEmpty()) {
      return;
    }
    for (int i = 0; i < swappableBooks.size(); i++) {
      var coverPhoto = swappableBooks.get(i).getCoverPhotoFile();
      var swappableBookId = bookDao.getSwapCondition().getSwappableBooks().get(i).getId();
      String uniqueId = photoService.addBookCoverPhoto(coverPhoto, swappableBookId);
      bookDao.getSwapCondition().getSwappableBooks().get(i).setCoverPhoto(uniqueId);
    }
    bookRepository.save(bookDao);
  }

  private void updateExistingDaoWithNewProperties(Book updatedBook, BookDao existingBookDao) {
    existingBookDao.setTitle(updatedBook.getTitle());
    existingBookDao.setAuthor(updatedBook.getAuthor());
    existingBookDao.setDescription(updatedBook.getDescription());
    existingBookDao.setLanguage(updatedBook.getLanguage().getCode());
    existingBookDao.setCondition(updatedBook.getCondition().getCode());
    addGenresToBook(updatedBook, existingBookDao);
    setValidSwappableGenresIfExists(updatedBook);
    existingBookDao.setSwapCondition(SwapConditionMapper.toDao(updatedBook.getSwapCondition()));
  }

  private void setValidSwappableGenresIfExists(Book book) {
    var swappableGenres = book.getSwapCondition().getSwappableGenres();
    if (swappableGenres == null || swappableGenres.isEmpty()) {
      return;
    }
    var validGenres = swappableGenres.stream()
        .map(genre -> genreRepository.findByName(genre.getName())
            .orElseThrow(() -> new GenreNotFoundException(genre.getName())))
        .toList();
    book.getSwapCondition().setSwappableGenres(validGenres.stream().map(GenreMapper::toEntity).toList());
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

  private BookDao updateBookCoverPhoto(Book book, BookDao dao) {
    deleteExistingCoverPhoto(dao);
    return addCoverPhotoToBook(book, dao);
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
    fetchImageUrlForSwappableBooksIfExists(book);
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

  private void fetchImageUrlForSwappableBooksIfExists(Book parentBook) {
    var swappableBooks = parentBook.getSwapCondition().getSwappableBooks();
    if (swappableBooks == null || swappableBooks.isEmpty()) {
      return;
    }
    for (int i = 0; i < swappableBooks.size(); i++) {
      var uniqueId = swappableBooks.get(i).getCoverPhoto();
      String coverPhotoUrl = photoService.getBookCoverPhoto(uniqueId);
      parentBook.getSwapCondition().getSwappableBooks().get(i).setCoverPhoto(coverPhotoUrl);
    }
  }

  private Page<Book> mapToBookPage(Page<BookDao> bookDaos, Pageable pageable) {
    var books = bookDaos.stream().map(this::bookWithImageUrlAndOwner).toList();
    return new PageImpl<>(books, pageable, bookDaos.getTotalElements());
  }

  public List<Book> getMoreBooksOfTheUser(String bookId) {
    var bookDao = bookRepository.findByIdAndIsDeletedFalse(bookId)
        .orElseThrow(() -> new BookNotFoundException(bookId));
    var owner = bookDao.getOwner();
    assert owner.getBooks() != null;
    return owner.getBooks().stream()
        .filter(book -> !book.getId().equals(bookId)) // Exclude the current book
        .map(this::bookWithImageUrlAndOwner).toList();
  }
}