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
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.BookMapper;
import com.kirjaswappi.backend.mapper.PhotoMapper;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.exceptions.BookNotFoundException;
import com.kirjaswappi.backend.service.exceptions.GenreNotFoundException;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;

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

  public Book createBook(Book book) throws IOException {
    // convert book to dao without genres, owner and cover photo:
    var bookDao = BookMapper.toDao(book);
    // add genres to book:
    addGenresToTheBook(book, bookDao);
    // set the owner of the book:
    addOwnerToTheBook(book, bookDao);
    // add cover photo to the book if any:
    addCoverPhotoToTheBookIfAny(book, bookDao);
    var savedDao = bookRepository.save(bookDao);
    return BookMapper.toEntity(savedDao);
  }

  private void addOwnerToTheBook(Book book, BookDao bookDao) {
    var owner = userRepository.findById(book.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(book.getOwner().getId()));
    bookDao.setOwner(owner);
  }

  private void addGenresToTheBook(Book book, BookDao bookDao) {
    bookDao.setGenres(book.getGenres().stream()
        .map(genreName -> genreRepository.findByName(genreName)
            .orElseThrow(() -> new GenreNotFoundException(genreName)))
        .toList());
  }

  private void addCoverPhotoToTheBookIfAny(Book book, BookDao dao) throws IOException {
    if (book.getCoverPhoto() != null) {
      deleteExistingCoverPhoto(dao);
      var photo = photoService.addBookCoverPhoto(book.getCoverPhoto().getFile());
      dao.setCoverPhoto(PhotoMapper.toDao(photo));
    }
  }

  private void deleteExistingCoverPhoto(BookDao dao) {
    // This ensures we don't have dangling cover photos for a book
    if (dao.getCoverPhoto() != null) {
      photoService.deleteBookCoverPhoto(PhotoMapper.toEntity(dao.getCoverPhoto()));
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
    // convert book to dao without genres, owner and cover photo:
    var dao = BookMapper.toDao(book);
    findAndSetOwnerToTheBook(book, dao);
    addGenresToTheBook(book, dao);
    addCoverPhotoToTheBookIfAny(book, dao);
    var updatedBook = bookRepository.save(dao);
    return BookMapper.toEntity(updatedBook);
  }

  private void findAndSetOwnerToTheBook(Book book, BookDao dao) {
    var owner = bookRepository.findById(book.getId())
        .orElseThrow(() -> new BookNotFoundException(book.getId())).getOwner();
    dao.setOwner(owner);
  }

  public void deleteBook(String id) {
    var dao = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    deleteExistingCoverPhoto(dao);
    bookRepository.deleteById(id);
  }
}
