/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.BOOKS;
import static com.kirjaswappi.backend.common.utils.Constants.ID;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.http.dtos.requests.CreateBookRequest;
import com.kirjaswappi.backend.http.dtos.requests.UpdateBookRequest;
import com.kirjaswappi.backend.http.dtos.responses.BookListResponse;
import com.kirjaswappi.backend.http.dtos.responses.BookResponse;
import com.kirjaswappi.backend.service.BookService;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@RestController
@RequestMapping(API_BASE + BOOKS)
public class BookController {
  @Autowired
  private BookService bookService;

  @PostMapping
  public ResponseEntity<BookResponse> createBook(@ModelAttribute CreateBookRequest book) throws IOException {
    Book savedBook = bookService.createBook(book.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(new BookResponse(savedBook));
  }

  @GetMapping(ID)
  public ResponseEntity<BookResponse> getBookById(@PathVariable String id) {
    Book book = bookService.getBookById(id);
    return ResponseEntity.status(HttpStatus.OK).body(new BookResponse(book));
  }

  @GetMapping
  public ResponseEntity<List<BookListResponse>> getAllBooks() {
    List<Book> books = bookService.getAllBooks();
    return ResponseEntity.status(HttpStatus.OK).body(books.stream().map(BookListResponse::new).toList());
  }

  @PutMapping(ID)
  public ResponseEntity<BookResponse> updateBook(@PathVariable String id, @ModelAttribute UpdateBookRequest book)
      throws IOException {
    // validate id:
    if (!id.equals(book.getId())) {
      throw new BadRequestException("idMismatch", id, book.getId());
    }
    Book updatedBook = bookService.updateBook(book.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new BookResponse(updatedBook));
  }

  @DeleteMapping(ID)
  public ResponseEntity<Void> deleteBook(@PathVariable String id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }
}