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
import com.kirjaswappi.backend.mapper.PhotoMapper;
import com.kirjaswappi.backend.mapper.UserMapper;
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
  @Autowired
  private UserService userService;

  public Book createBook(Book book) throws IOException {
    // convert book to dao without genres, owner and cover photo:
    var bookDao = BookMapper.toDao(book);
    // add genres to book:
    addGenresToTheBook(book, bookDao);
    // set the owner of the book:
    var owner = userService.getUser(book.getOwner().getId());
    bookDao.setOwner(UserMapper.toDao(owner));
    // save the book without cover photo first:
    bookDao.setCoverPhoto(null);
    bookRepository.save(bookDao);
    // set the id of the book
    book.setId(bookDao.getId());
    return addCoverPhotoToTheBookIfAny(book, bookDao);
  }

  private Book addCoverPhotoToTheBookIfAny(Book book, BookDao dao) throws IOException {
    if (book.getCoverPhoto() != null) {
      var photo = photoService.addBookPhoto(book.getId(), book.getCoverPhoto().getFile());
      dao.setCoverPhoto(PhotoMapper.toDao(photo));
      dao = bookRepository.save(dao);
    }
    return BookMapper.toEntity(dao);
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
    BookDao bookDao = BookMapper.toDao(book);
    addGenresToTheBook(book, bookDao);
    return addCoverPhotoToTheBookIfAny(book, bookDao);
  }

  private void addGenresToTheBook(Book book, BookDao bookDao) {
    // add genres to book:
    bookDao.setGenres(book.getGenres().stream()
        .map(genreName -> genreRepository.findByName(genreName)
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
