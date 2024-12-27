/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.BOOKS;
import static com.kirjaswappi.backend.common.utils.Constants.ID;
import static com.kirjaswappi.backend.common.utils.Constants.SUPPORTED_CONDITIONS;
import static com.kirjaswappi.backend.common.utils.Constants.SUPPORTED_LANGUAGES;

import java.io.IOException;
import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.kirjaswappi.backend.service.enums.Condition;
import com.kirjaswappi.backend.service.enums.Language;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;
import com.kirjaswappi.backend.service.filters.GetAllBooksFilter;

@RestController
@RequestMapping(API_BASE + BOOKS)
@Validated
public class BookController {
  @Autowired
  private BookService bookService;

  @PostMapping
  @Operation(summary = "Adds a book to a user.", responses = {
      @ApiResponse(responseCode = "201", description = "Book created.") })
  public ResponseEntity<BookResponse> createBook(@Valid @ModelAttribute CreateBookRequest book) throws IOException {
    Book savedBook = bookService.createBook(book.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(new BookResponse(savedBook));
  }

  @GetMapping(ID)
  @Operation(summary = "Get a book by Book Id.", responses = {
      @ApiResponse(responseCode = "200", description = "Book found.") })
  public ResponseEntity<BookResponse> getBookById(@Parameter(description = "Book Id.") @PathVariable String id) {
    Book book = bookService.getBookById(id);
    return ResponseEntity.status(HttpStatus.OK).body(new BookResponse(book));
  }

  @GetMapping(SUPPORTED_LANGUAGES)
  @Operation(summary = "Get a list of supported languages.", responses = {
      @ApiResponse(responseCode = "200", description = "List of supported languages.") })
  public ResponseEntity<List<String>> getAllSupportedLanguges() {
    return ResponseEntity.status(HttpStatus.OK).body(Language.getSupportedLanguages());
  }

  @GetMapping(SUPPORTED_CONDITIONS)
  @Operation(summary = "Get a list of supported book conditions.", responses = {
      @ApiResponse(responseCode = "200", description = "List of supported conditions.") })
  public ResponseEntity<List<String>> getAllSupportedConditions() {
    return ResponseEntity.status(HttpStatus.OK).body(Condition.getSupportedConditions());
  }

  @GetMapping
  @Operation(summary = "Search for books with (optional) filter properties.", responses = {
      @ApiResponse(responseCode = "200", description = "List of Books.") })
  public ResponseEntity<List<BookListResponse>> getAllBooks(@Valid @ParameterObject GetAllBooksFilter filter) {
    List<Book> books = (filter == null) ? bookService.getAllBooks() : bookService.getAllBooksByFilter(filter);
    return ResponseEntity.status(HttpStatus.OK).body(books.stream().map(BookListResponse::new).toList());
  }

  @PutMapping(ID)
  @Operation(summary = "Updates a book by Book Id.", responses = {
      @ApiResponse(responseCode = "200", description = "Book updated.") })
  public ResponseEntity<BookResponse> updateBook(@Parameter(description = "Book Id.") @PathVariable String id,
      @Valid @ModelAttribute UpdateBookRequest request)
      throws IOException {
    // validate id:
    if (!id.equals(request.getId())) {
      throw new BadRequestException("idMismatch", id, request.getId());
    }
    Book updatedBook = bookService.updateBook(request.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new BookResponse(updatedBook));
  }

  @DeleteMapping(ID)
  @Operation(summary = "Deletes a book by Book Id.", responses = {
      @ApiResponse(responseCode = "204", description = "Book deleted.") })
  public ResponseEntity<Void> deleteBook(@Parameter(description = "Book Id.") @PathVariable String id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }
}