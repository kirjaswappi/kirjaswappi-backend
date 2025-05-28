/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.jpa.daos.SwapConditionDao;
import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.jpa.repositories.BookRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.SwapCondition;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.enums.Condition;
import com.kirjaswappi.backend.service.enums.Language;
import com.kirjaswappi.backend.service.enums.SwapType;
import com.kirjaswappi.backend.service.exceptions.BookNotFoundException;
import com.kirjaswappi.backend.service.filters.FindAllBooksFilter;

class BookServiceTest {
  @Mock
  private BookRepository bookRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private BookService bookService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Throws when book is not found by ID")
  void getBookByIdThrowsWhenNotFound() {
    when(bookRepository.findByIdAndIsDeletedFalse("id")).thenReturn(Optional.empty());
    assertThrows(BookNotFoundException.class, () -> bookService.getBookById("id"));
  }

  @Test
  @DisplayName("Returns book when found by ID")
  void getBookByIdReturnsBookWhenFound() {
    var dao = new BookDao();
    dao.setId("id");
    dao.setSwapCondition(
        new SwapConditionDao("ByBooks", false, false, List.of(), List.of()));
    dao.setOwner(new UserDao());
    dao.getOwner().setId("owner-id");
    dao.setLanguage("English");
    dao.setCondition("New");
    dao.setGenres(List.of());
    dao.setCoverPhotos(List.of());
    when(bookRepository.findByIdAndIsDeletedFalse("id")).thenReturn(Optional.of(dao));
    Book book = bookService.getBookById("id");
    assertEquals("id", book.getId());
  }

  @Test
  @DisplayName("Returns page of books by filter")
  void getAllBooksByFilterReturnsPage() {
    FindAllBooksFilter filter = mock(FindAllBooksFilter.class);
    Pageable pageable = PageRequest.of(0, 10);
    when(filter.buildSearchAndFilterCriteria()).thenReturn(null);
    when(bookRepository.findAllBooksByFilter(any(), any())).thenReturn(new PageImpl<>(List.of()));
    Page<Book> result = bookService.getAllBooksByFilter(filter, pageable);
    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
  }

  @Test
  @DisplayName("Saves a new book")
  void createBookSavesBook() {
    Book book = new Book();
    book.setSwapCondition(new SwapCondition(
        SwapType.OPEN_FOR_OFFERS, false, true, null, null));
    book.setLanguage(Language.ENGLISH);
    book.setCondition(Condition.NEW);
    book.setGenres(List.of());
    book.setCoverPhotos(List.of());
    book.setCoverPhotoFiles(List.of());
    book.setOwner(new User());
    UserDao userDao = new UserDao();
    userDao.setId("owner-id");
    userDao.setFirstName("Test");
    userDao.setLastName("User");
    userDao.setEmail("test@example.com");
    userDao.setPassword("password");
    userDao.setSalt("salt");
    userDao.setEmailVerified(true);
    when(userRepository.findByIdAndIsEmailVerifiedTrue(any())).thenReturn(Optional.of(userDao));
    var bookDao = new BookDao();
    bookDao.setId("id");
    bookDao.setSwapCondition(
        new SwapConditionDao("OpenForOffers", false, true, null, null));
    bookDao.setOwner(userDao);
    bookDao.setLanguage("English");
    bookDao.setCondition("New");
    bookDao.setGenres(List.of());
    bookDao.setCoverPhotos(List.of());
    when(bookRepository.save(any())).thenReturn(bookDao);
    when(bookRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.of(bookDao));
    assertNotNull(bookService.createBook(book));
  }

  @Test
  @DisplayName("Updates an existing book")
  void updateBookUpdatesBook() {
    Book book = new Book();
    book.setId("id");
    book.setSwapCondition(new SwapCondition(
        SwapType.OPEN_FOR_OFFERS, false, true, null, null));
    book.setLanguage(Language.ENGLISH);
    book.setCondition(Condition.NEW);
    book.setGenres(List.of());
    book.setCoverPhotos(List.of());
    book.setCoverPhotoFiles(List.of());
    book.setOwner(new User());
    var dao = new BookDao();
    dao.setId("id");
    dao.setSwapCondition(
        new SwapConditionDao("OpenForOffers", false, true, null, null));
    dao.setOwner(new UserDao());
    dao.setLanguage("English");
    dao.setCondition("New");
    dao.setGenres(List.of());
    dao.setCoverPhotos(List.of());
    when(bookRepository.findByIdAndIsDeletedFalse("id")).thenReturn(Optional.of(dao));
    when(bookRepository.save(any())).thenReturn(dao);
    assertNotNull(bookService.updateBook(book));
  }

  @Test
  @DisplayName("Throws when updating a non-existent book")
  void updateBookThrowsWhenNotFound() {
    Book book = new Book();
    book.setId("id");
    when(bookRepository.findByIdAndIsDeletedFalse("id")).thenReturn(Optional.empty());
    assertThrows(BookNotFoundException.class, () -> bookService.updateBook(book));
  }

  @Test
  @DisplayName("Deletes a book by ID")
  void deleteBookDeletesBook() {
    var dao = new BookDao();
    dao.setId("id");
    dao.setOwner(new UserDao());
    dao.setSwapCondition(
        new SwapConditionDao("ByBooks", false, false, List.of(), List.of()));
    dao.setLanguage("English");
    dao.setCondition("New");
    dao.setGenres(List.of());
    dao.setCoverPhotos(List.of());
    when(bookRepository.findByIdAndIsDeletedFalse("id")).thenReturn(Optional.of(dao));
    UserDao userDao = new UserDao();
    userDao.setId("owner-id");
    userDao.setFirstName("Test");
    userDao.setLastName("User");
    userDao.setEmail("test@example.com");
    userDao.setPassword("password");
    userDao.setSalt("salt");
    userDao.setEmailVerified(true);
    when(userRepository.findByIdAndIsEmailVerifiedTrue(any())).thenReturn(Optional.of(userDao));
    doNothing().when(bookRepository).deleteLogically("id");
    bookService.deleteBook("id");
    verify(bookRepository, times(1)).deleteLogically("id");
  }

  @Test
  void deleteBookThrowsWhenNotFound() {
    when(bookRepository.findByIdAndIsDeletedFalse("id")).thenReturn(Optional.empty());
    assertThrows(BookNotFoundException.class, () -> bookService.deleteBook("id"));
  }
}
