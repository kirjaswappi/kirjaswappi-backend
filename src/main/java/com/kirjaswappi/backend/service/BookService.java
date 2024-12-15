/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.jpa.repositories.BookRepository;
import com.kirjaswappi.backend.jpa.repositories.GenreRepository;
import com.kirjaswappi.backend.mapper.BookMapper;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.exceptions.BookNotFoundException;
import com.kirjaswappi.backend.service.exceptions.GenreNotFoundException;

@Service
@Transactional
public class BookService {
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private GenreRepository genreRepository;
  @Autowired
  private PhotoService photoService;

  public Book createBook(Book book) throws IOException {
    this.addCoverPhotoToTheBookIfAny(book);
    var bookDao = BookMapper.toDao(book);
    // add genres to book:
    this.addGenresToTheBook(book, bookDao);
    return BookMapper.toEntity(bookRepository.save(bookDao));
  }

  private void addCoverPhotoToTheBookIfAny(Book book) throws IOException {
    if (book.getCoverPhoto() != null) {
      // This ensures we don't have multiple cover photos for a book
      photoService.deleteBookPhoto(book.getCoverPhoto().getId());
      var photo = photoService.addBookPhoto(book.getId(), book.getCoverPhoto().getFile());
      book.setCoverPhoto(photo);
    }
  }

  public Book getBookById(String id) {
    return BookMapper.toEntity(bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id)));
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll().stream()
        .map(BookMapper::toEntity).toList();
  }

  public Book updateBook(Book book) throws IOException {
    this.addCoverPhotoToTheBookIfAny(book);
    BookDao bookDao = BookMapper.toDao(book);
    addGenresToTheBook(book, bookDao);
    return BookMapper.toEntity(bookRepository.save(bookDao));
  }

  private void addGenresToTheBook(Book book, BookDao bookDao) {
    // add genres to book:
    bookDao.setGenres(book.getGenres().stream()
        .map(genreName -> genreRepository.findByNameIgnoreCase(genreName)
            .orElseThrow(() -> new GenreNotFoundException(genreName)))
        .toList());
  }

  public void deleteBook(String id) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException(id);
    }
    bookRepository.deleteById(id);
  }
}
