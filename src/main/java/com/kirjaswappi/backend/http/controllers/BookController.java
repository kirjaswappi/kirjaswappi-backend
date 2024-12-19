/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.BOOKS;
import static com.kirjaswappi.backend.common.utils.Constants.COVER_PHOTO;
import static com.kirjaswappi.backend.common.utils.Constants.ID;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.kirjaswappi.backend.service.PhotoService;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;
import com.kirjaswappi.backend.service.filters.GetAllBooksFilter;

@RestController
@RequestMapping(API_BASE + BOOKS)
public class BookController {
  @Autowired
  private BookService bookService;
  @Autowired
  private PhotoService photoService;

  @PostMapping
  @Operation(summary = "Adds a book to a user.", responses = {
      @ApiResponse(responseCode = "201", description = "Book created.") })
  public ResponseEntity<BookResponse> createBook(@ModelAttribute CreateBookRequest book) throws IOException {
    Book savedBook = bookService.createBook(book.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(new BookResponse(savedBook));
  }

  @GetMapping(ID)
  @Operation(summary = "Get a book by Book Id.", responses = {
      @ApiResponse(responseCode = "200", description = "Book found.") })
  public ResponseEntity<BookResponse> getBookById(@PathVariable String id) {
    Book book = bookService.getBookById(id);
    return ResponseEntity.status(HttpStatus.OK).body(new BookResponse(book));
  }

  @GetMapping(ID + COVER_PHOTO)
  @Operation(summary = "Get a book cover photo by Book Id.", responses = {
      @ApiResponse(responseCode = "200", description = "Cover photo found.") })
  public ResponseEntity<byte[]> getBookCoverPhotoById(@PathVariable String id) {
    return getPhotoResponse(photoService.getBookCoverById(id));
  }

  @GetMapping
  @Operation(summary = "Searches for books with optional filter properties.", responses = {
      @ApiResponse(responseCode = "200", description = "List of Books.") })
  public ResponseEntity<List<BookListResponse>> getAllBooks(@ParameterObject GetAllBooksFilter filter) {
    List<Book> books = (filter == null) ? bookService.getAllBooks() : bookService.getAllBooksByFilter(filter);
    return ResponseEntity.status(HttpStatus.OK).body(books.stream().map(BookListResponse::new).toList());
  }

  @GetMapping(COVER_PHOTO)
  @Operation(summary = "Get cover photos of all books as a zip.", responses = {
      @ApiResponse(responseCode = "200", description = "Cover photos found.") })
  public ResponseEntity<byte[]> getCoverPhotosOfAllBooks() throws IOException {
    List<String> bookIds = bookService.getAllBooks().stream().map(Book::getId).toList();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

    for (String bookId : bookIds) {
      byte[] photo = photoService.getBookCoverById(bookId);
      ZipEntry zipEntry = new ZipEntry(bookId + ".jpg");
      zipOutputStream.putNextEntry(zipEntry);
      zipOutputStream.write(photo);
      zipOutputStream.closeEntry();
    }

    zipOutputStream.close();
    byte[] zipBytes = byteArrayOutputStream.toByteArray();

    return ResponseEntity.ok()
        .contentType(MediaType.valueOf("application/zip"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cover_photos.zip")
        .body(zipBytes);
  }

  @PutMapping(ID)
  @Operation(summary = "Updates a book by Book Id.", responses = {
      @ApiResponse(responseCode = "200", description = "Book updated.") })
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
  @Operation(summary = "Deletes a book by Book Id.", responses = {
      @ApiResponse(responseCode = "204", description = "Book deleted.") })
  public ResponseEntity<Void> deleteBook(@PathVariable String id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }

  private ResponseEntity<byte[]> getPhotoResponse(byte[] photoBytes) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=CoverPhoto.jpg")
        .body(photoBytes);
  }
}