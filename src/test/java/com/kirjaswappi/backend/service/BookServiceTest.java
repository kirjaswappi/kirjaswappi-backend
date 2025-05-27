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

import com.kirjaswappi.backend.jpa.repositories.BookRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.exceptions.BookNotFoundException;
import com.kirjaswappi.backend.service.filters.FindAllBooksFilter;

class BookServiceTest {
  @Mock
  private BookRepository bookRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private GenreService genreService;
  @Mock
  private PhotoService photoService;

  @InjectMocks
  private BookService bookService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should throw BookNotFoundException when book not found by id")
  void getBookByIdThrowsWhenNotFound() {
    when(bookRepository.findByIdAndIsDeletedFalse("id")).thenReturn(Optional.empty());
    assertThrows(BookNotFoundException.class, () -> bookService.getBookById("id"));
  }

  @Test
  @DisplayName("Should return paged books by filter")
  void getAllBooksByFilterReturnsPage() {
    FindAllBooksFilter filter = mock(FindAllBooksFilter.class);
    Pageable pageable = PageRequest.of(0, 10);
    when(filter.buildSearchAndFilterCriteria()).thenReturn(null);
    when(bookRepository.findAllBooksByFilter(any(), any())).thenReturn(new PageImpl<>(List.of()));
    Page<Book> result = bookService.getAllBooksByFilter(filter, pageable);
    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
  }

  // Add more tests for createBook, updateBook, deleteBook, etc.
}
