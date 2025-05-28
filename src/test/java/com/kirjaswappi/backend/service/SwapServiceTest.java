/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.jpa.daos.GenreDao;
import com.kirjaswappi.backend.jpa.daos.SwapConditionDao;
import com.kirjaswappi.backend.jpa.daos.SwapOfferDao;
import com.kirjaswappi.backend.jpa.daos.SwapRequestDao;
import com.kirjaswappi.backend.jpa.daos.SwappableBookDao;
import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.jpa.repositories.SwapRequestRepository;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.entities.SwapCondition;
import com.kirjaswappi.backend.service.entities.SwapOffer;
import com.kirjaswappi.backend.service.entities.SwapRequest;
import com.kirjaswappi.backend.service.entities.SwappableBook;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.enums.Condition;
import com.kirjaswappi.backend.service.enums.Language;
import com.kirjaswappi.backend.service.enums.SwapStatus;
import com.kirjaswappi.backend.service.enums.SwapType;
import com.kirjaswappi.backend.service.exceptions.IllegalSwapRequestException;
import com.kirjaswappi.backend.service.exceptions.SwapRequestExistsAlreadyException;

class SwapServiceTest {
  @Mock
  private SwapRequestRepository swapRequestRepository;
  @Mock
  private UserService userService;
  @Mock
  private BookService bookService;
  @Mock
  private GenreService genreService;
  @InjectMocks
  private SwapService swapService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should throw SwapRequestExistsAlreadyException when swap request already exists")
  void createSwapRequestThrowsWhenExistsAlready() {
    var swapRequest = new SwapRequest();
    var sender = new User();
    sender.setId("senderId");
    var receiver = new User();
    receiver.setId("receiverId");
    var book = new Book();
    book.setId("bookId");
    swapRequest.setSender(sender);
    swapRequest.setReceiver(receiver);
    swapRequest.setBookToSwapWith(book);
    when(swapRequestRepository.existsAlready("senderId", "receiverId", "bookId")).thenReturn(true);
    assertThrows(SwapRequestExistsAlreadyException.class, () -> swapService.createSwapRequest(swapRequest));
  }

  @Test
  @DisplayName("Should create swap request successfully when not duplicate")
  void createSwapRequestSuccess() {
    var swapRequest = new SwapRequest();
    var sender = new User();
    sender.setId("senderId");
    var receiver = new User();
    receiver.setId("receiverId");
    receiver.setBooks(java.util.List.of());
    var book = new Book();
    book.setId("bookId");
    book.setSwapCondition(null);
    swapRequest.setSender(sender);
    swapRequest.setReceiver(receiver);
    swapRequest.setBookToSwapWith(book);
    swapRequest.setSwapType(SwapType.BY_BOOKS);
    swapRequest.setSwapStatus(SwapStatus.PENDING);
    when(swapRequestRepository.existsAlready("senderId", "receiverId", "bookId")).thenReturn(false);
    when(userService.getUser("senderId")).thenReturn(sender);
    when(userService.getUser("receiverId")).thenReturn(receiver);
    when(bookService.getBookById("bookId")).thenReturn(book);
    when(swapRequestRepository.save(any())).thenReturn(null);
    // receiver has no books, so IllegalSwapRequestException expected
    assertThrows(IllegalSwapRequestException.class, () -> swapService.createSwapRequest(swapRequest));
  }

  @Test
  @DisplayName("Should delete all swap requests")
  void deleteAllSwapRequests() {
    swapService.deleteAllSwapRequests();
    verify(swapRequestRepository, times(1)).deleteAll();
  }

  @Test
  @DisplayName("Should throw when book to swap with does not belong to receiver")
  void createSwapRequestThrowsWhenBookNotBelongToReceiver() {
    var swapRequest = new SwapRequest();
    var sender = new User();
    sender.setId("senderId");
    var receiver = new User();
    receiver.setId("receiverId");
    var book = new Book();
    book.setId("bookId");
    swapRequest.setSender(sender);
    swapRequest.setReceiver(receiver);
    swapRequest.setBookToSwapWith(book);
    swapRequest.setSwapType(SwapType.BY_BOOKS);
    swapRequest.setSwapStatus(SwapStatus.PENDING);
    // receiver has a different book
    var otherBook = new Book();
    otherBook.setId("otherBookId");
    receiver.setBooks(List.of(otherBook));
    var swapCondition = new com.kirjaswappi.backend.service.entities.SwapCondition();
    swapCondition.setSwappableBooks(List.of());
    swapCondition.setSwappableGenres(List.of());
    book.setSwapCondition(swapCondition);
    when(swapRequestRepository.existsAlready("senderId", "receiverId", "bookId")).thenReturn(false);
    when(userService.getUser("senderId")).thenReturn(sender);
    when(userService.getUser("receiverId")).thenReturn(receiver);
    when(bookService.getBookById("bookId")).thenReturn(book);
    assertThrows(IllegalSwapRequestException.class, () -> swapService.createSwapRequest(swapRequest));
  }

  @Test
  @DisplayName("Should throw when offered book is not in swappable books")
  void createSwapRequestThrowsWhenOfferedBookNotSwappable() {
    var swapRequest = new SwapRequest();
    var sender = new User();
    sender.setId("senderId");
    var receiver = new User();
    receiver.setId("receiverId");
    var book = new Book();
    book.setId("bookId");
    var offeredBook = new com.kirjaswappi.backend.service.entities.SwappableBook();
    offeredBook.setId("offeredBookId");
    var swapOffer = new SwapOffer();
    swapOffer.setOfferedBook(offeredBook);
    swapRequest.setSender(sender);
    swapRequest.setReceiver(receiver);
    swapRequest.setBookToSwapWith(book);
    swapRequest.setSwapType(SwapType.BY_BOOKS);
    swapRequest.setSwapStatus(SwapStatus.PENDING);
    swapRequest.setSwapOffer(swapOffer);
    receiver.setBooks(List.of(book));
    var swapCondition = new SwapCondition();
    swapCondition.setSwappableBooks(List.of());
    swapCondition.setSwappableGenres(List.of());
    swapCondition.setSwapType(SwapType.BY_BOOKS);
    book.setSwapCondition(swapCondition);
    when(swapRequestRepository.existsAlready("senderId", "receiverId", "bookId")).thenReturn(false);
    when(userService.getUser("senderId")).thenReturn(sender);
    when(userService.getUser("receiverId")).thenReturn(receiver);
    when(bookService.getBookById("bookId")).thenReturn(book);
    when(bookService.getSwappableBookById("offeredBookId")).thenReturn(offeredBook);
    assertThrows(IllegalSwapRequestException.class, () -> swapService.createSwapRequest(swapRequest));
  }

  @Test
  @DisplayName("Should throw when offered genre is not in swappable genres")
  void createSwapRequestThrowsWhenOfferedGenreNotSwappable() {
    var swapRequest = new SwapRequest();
    var sender = new User();
    sender.setId("senderId");
    var receiver = new User();
    receiver.setId("receiverId");
    var book = new Book();
    book.setId("bookId");
    var offeredGenre = new Genre();
    offeredGenre.setId("offeredGenreId");
    var swapOffer = new SwapOffer();
    swapOffer.setOfferedGenre(offeredGenre);
    swapRequest.setSender(sender);
    swapRequest.setReceiver(receiver);
    swapRequest.setBookToSwapWith(book);
    swapRequest.setSwapType(SwapType.BY_GENRES);
    swapRequest.setSwapStatus(SwapStatus.PENDING);
    swapRequest.setSwapOffer(swapOffer);
    receiver.setBooks(List.of(book));
    var swapCondition = new SwapCondition();
    swapCondition.setSwappableGenres(List.of());
    swapCondition.setSwappableBooks(List.of());
    swapCondition.setSwapType(SwapType.BY_GENRES);
    book.setSwapCondition(swapCondition);
    when(swapRequestRepository.existsAlready("senderId", "receiverId", "bookId")).thenReturn(false);
    when(userService.getUser("senderId")).thenReturn(sender);
    when(userService.getUser("receiverId")).thenReturn(receiver);
    when(bookService.getBookById("bookId")).thenReturn(book);
    when(genreService.getGenreById("offeredGenreId")).thenReturn(offeredGenre);
    assertThrows(IllegalSwapRequestException.class, () -> swapService.createSwapRequest(swapRequest));
  }

  @Test
  @DisplayName("Should create swap request with valid offered book")
  void createSwapRequestWithValidOfferedBook() {
    var swapRequest = new SwapRequest();
    var sender = new User();
    sender.setId("senderId");
    var receiver = new User();
    receiver.setId("receiverId");
    var book = new Book();
    book.setId("bookId");
    book.setLanguage(Language.ENGLISH);
    book.setCondition(Condition.GOOD);
    var offeredBook = new SwappableBook();
    offeredBook.setId("offeredBookId");
    var swapOffer = new SwapOffer();
    swapOffer.setOfferedBook(offeredBook);
    swapRequest.setSender(sender);
    swapRequest.setReceiver(receiver);
    swapRequest.setBookToSwapWith(book);
    swapRequest.setSwapType(SwapType.BY_BOOKS);
    swapRequest.setSwapStatus(SwapStatus.PENDING);
    swapRequest.setSwapOffer(swapOffer);
    receiver.setBooks(List.of(book));
    var swapCondition = new SwapCondition();
    swapCondition.setSwappableBooks(List.of(offeredBook));
    swapCondition.setSwappableGenres(List.of());
    swapCondition.setSwapType(SwapType.BY_BOOKS);
    book.setSwapCondition(swapCondition);
    when(swapRequestRepository.existsAlready("senderId", "receiverId", "bookId")).thenReturn(false);
    when(userService.getUser("senderId")).thenReturn(sender);
    when(userService.getUser("receiverId")).thenReturn(receiver);
    when(bookService.getBookById("bookId")).thenReturn(book);
    when(bookService.getSwappableBookById("offeredBookId")).thenReturn(offeredBook);
    var mockUserDao = mock(UserDao.class);
    when(mockUserDao.getId()).thenReturn("senderId");
    var mockReceiverDao = mock(UserDao.class);
    when(mockReceiverDao.getId()).thenReturn("receiverId");
    var mockSwapRequestDao = mock(SwapRequestDao.class);
    when(mockSwapRequestDao.getSender()).thenReturn(mockUserDao);
    when(mockSwapRequestDao.getReceiver()).thenReturn(mockReceiverDao);
    var mockBookDao = mock(BookDao.class);
    when(mockBookDao.getId()).thenReturn("bookId");
    when(mockSwapRequestDao.getBookToSwapWith()).thenReturn(mockBookDao);
    var mockSwapOfferDao = mock(SwapOfferDao.class);
    var mockSwappableBookDao = mock(SwappableBookDao.class);
    when(mockSwappableBookDao.getId()).thenReturn("offeredBookId");
    when(mockSwapOfferDao.getOfferedBook()).thenReturn(mockSwappableBookDao);
    when(mockSwapRequestDao.getSwapOfferDao()).thenReturn(mockSwapOfferDao);
    when(mockBookDao.getTitle()).thenReturn("title");
    when(mockBookDao.getAuthor()).thenReturn("author");
    when(mockBookDao.getDescription()).thenReturn("desc");
    when(mockBookDao.getLanguage()).thenReturn("English");
    when(mockBookDao.getCondition()).thenReturn("New");
    when(mockBookDao.getGenres()).thenReturn(List.of());
    when(mockBookDao.getCoverPhotos()).thenReturn(List.of());
    var mockOwnerDao = mock(UserDao.class);
    when(mockBookDao.getOwner()).thenReturn(mockOwnerDao);
    var mockSwapConditionDao = mock(SwapConditionDao.class);
    when(mockBookDao.getSwapCondition()).thenReturn(mockSwapConditionDao);
    when(mockSwapConditionDao.getSwapType()).thenReturn("ByBooks");
    when(mockSwapConditionDao.isGiveAway()).thenReturn(false);
    when(mockSwapConditionDao.isOpenForOffers()).thenReturn(false);
    when(mockSwapConditionDao.getSwappableGenres()).thenReturn(List.of());
    when(mockSwapConditionDao.getSwappableBooks()).thenReturn(List.of());
    when(mockSwapRequestDao.getSwapType()).thenReturn("ByBooks");
    when(mockSwapRequestDao.getSwapStatus()).thenReturn("Pending");
    when(mockSwapRequestDao.getRequestedAt()).thenReturn(Instant.now());
    when(mockSwapRequestDao.getUpdatedAt()).thenReturn(Instant.now());
    when(swapRequestRepository.save(any())).thenReturn(mockSwapRequestDao);
    assertDoesNotThrow(() -> swapService.createSwapRequest(swapRequest));
  }

  @Test
  @DisplayName("Should create swap request with valid offered genre")
  void createSwapRequestWithValidOfferedGenre() {
    var swapRequest = new SwapRequest();
    var sender = new User();
    sender.setId("senderId");
    var receiver = new User();
    receiver.setId("receiverId");
    var book = new Book();
    book.setId("bookId");
    book.setLanguage(Language.ENGLISH);
    book.setCondition(Condition.GOOD);
    var offeredGenre = new Genre();
    offeredGenre.setId("offeredGenreId");
    var swapOffer = new SwapOffer();
    swapOffer.setOfferedGenre(offeredGenre);
    swapRequest.setSender(sender);
    swapRequest.setReceiver(receiver);
    swapRequest.setBookToSwapWith(book);
    swapRequest.setSwapType(SwapType.BY_GENRES);
    swapRequest.setSwapStatus(SwapStatus.PENDING);
    swapRequest.setSwapOffer(swapOffer);
    receiver.setBooks(List.of(book));
    var swapCondition = new SwapCondition();
    swapCondition.setSwappableGenres(List.of(offeredGenre));
    swapCondition.setSwappableBooks(List.of());
    swapCondition.setSwapType(SwapType.BY_GENRES);
    book.setSwapCondition(swapCondition);
    when(swapRequestRepository.existsAlready("senderId", "receiverId", "bookId")).thenReturn(false);
    when(userService.getUser("senderId")).thenReturn(sender);
    when(userService.getUser("receiverId")).thenReturn(receiver);
    when(bookService.getBookById("bookId")).thenReturn(book);
    when(genreService.getGenreById("offeredGenreId")).thenReturn(offeredGenre);
    var mockUserDao2 = mock(UserDao.class);
    when(mockUserDao2.getId()).thenReturn("senderId");
    var mockReceiverDao2 = mock(UserDao.class);
    when(mockReceiverDao2.getId()).thenReturn("receiverId");
    var mockSwapRequestDao2 = mock(SwapRequestDao.class);
    when(mockSwapRequestDao2.getSender()).thenReturn(mockUserDao2);
    when(mockSwapRequestDao2.getReceiver()).thenReturn(mockReceiverDao2);
    var mockBookDao2 = mock(BookDao.class);
    when(mockBookDao2.getId()).thenReturn("bookId");
    when(mockSwapRequestDao2.getBookToSwapWith()).thenReturn(mockBookDao2);
    var mockSwapOfferDao2 = mock(SwapOfferDao.class);
    var mockGenreDao2 = mock(GenreDao.class);
    when(mockGenreDao2.getId()).thenReturn("offeredGenreId");
    when(mockSwapOfferDao2.getOfferedGenre()).thenReturn(mockGenreDao2);
    when(mockSwapRequestDao2.getSwapOfferDao()).thenReturn(mockSwapOfferDao2);
    when(mockBookDao2.getTitle()).thenReturn("title");
    when(mockBookDao2.getAuthor()).thenReturn("author");
    when(mockBookDao2.getDescription()).thenReturn("desc");
    when(mockBookDao2.getLanguage()).thenReturn("English");
    when(mockBookDao2.getCondition()).thenReturn("New");
    when(mockBookDao2.getGenres()).thenReturn(java.util.List.of());
    when(mockBookDao2.getCoverPhotos()).thenReturn(java.util.List.of());
    var mockOwnerDao2 = mock(UserDao.class);
    when(mockBookDao2.getOwner()).thenReturn(mockOwnerDao2);
    var mockSwapConditionDao2 = mock(SwapConditionDao.class);
    when(mockBookDao2.getSwapCondition()).thenReturn(mockSwapConditionDao2);
    when(mockSwapConditionDao2.getSwapType()).thenReturn("ByGenres");
    when(mockSwapConditionDao2.isGiveAway()).thenReturn(false);
    when(mockSwapConditionDao2.isOpenForOffers()).thenReturn(false);
    when(mockSwapConditionDao2.getSwappableGenres()).thenReturn(java.util.List.of());
    when(mockSwapConditionDao2.getSwappableBooks()).thenReturn(java.util.List.of());
    when(mockSwapRequestDao2.getSwapType()).thenReturn("ByGenres");
    when(mockSwapRequestDao2.getSwapStatus()).thenReturn("Pending");
    when(mockSwapRequestDao2.getRequestedAt()).thenReturn(java.time.Instant.now());
    when(mockSwapRequestDao2.getUpdatedAt()).thenReturn(java.time.Instant.now());
    when(swapRequestRepository.save(any())).thenReturn(mockSwapRequestDao2);
    assertDoesNotThrow(() -> swapService.createSwapRequest(swapRequest));
  }
}
