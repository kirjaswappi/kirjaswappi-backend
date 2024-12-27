/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.jpa.repositories.BookRepository;
import com.kirjaswappi.backend.jpa.repositories.GenreRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.BookMapper;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.exceptions.BookNotFoundException;
import com.kirjaswappi.backend.service.exceptions.GenreNotFoundException;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;
import com.kirjaswappi.backend.service.filters.GetAllBooksFilter;

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

  public Book createBook(Book book) {
    var bookDao = BookMapper.toDao(book);
    this.addGenresToBook(book, bookDao);
    this.setOwnerToBook(book, bookDao);
    var savedDao = bookRepository.save(bookDao);
    savedDao = this.addCoverPhotoToBook(book, savedDao);
    this.addBookToOwner(savedDao);
    var imageUrl = photoService.getBookCoverPhoto(savedDao.getCoverPhoto());
    var newBook = BookMapper.toEntity(bookDao, imageUrl);
    return BookMapper.setOwner(bookDao.getOwner(), newBook);
  }

  public Book updateBook(Book book) {
    var dao = bookRepository.findById(book.getId())
        .orElseThrow(() -> new BookNotFoundException(book.getId()));
    this.updateDaoWithNewProperties(book, dao);
    var updatedBookDao = bookRepository.save(dao);
    updatedBookDao = this.addCoverPhotoToBook(book, updatedBookDao);
    var imageUrl = photoService.getBookCoverPhoto(updatedBookDao.getCoverPhoto());
    var updatedBook = BookMapper.toEntity(updatedBookDao, imageUrl);
    return BookMapper.setOwner(updatedBookDao.getOwner(), updatedBook);
  }

  private void updateDaoWithNewProperties(Book book, BookDao dao) {
    dao.setTitle(book.getTitle());
    dao.setAuthor(book.getAuthor());
    dao.setDescription(book.getDescription());
    dao.setLanguage(book.getLanguage().name());
    dao.setCondition(book.getCondition().name());
    addGenresToBook(book, dao);
  }

  public Book getBookById(String id) {
    var bookDao = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
    var imageUrl = photoService.getBookCoverPhoto(bookDao.getCoverPhoto());
    var book = BookMapper.toEntity(bookDao, imageUrl);
    return BookMapper.setOwner(bookDao.getOwner(), book);
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll().stream()
        .map(bookDao -> {
          var imageUrl = photoService.getBookCoverPhoto(bookDao.getCoverPhoto());
          return BookMapper.toEntity(bookDao, imageUrl);
        }).toList();
  }

  public List<Book> getAllBooksByFilter(GetAllBooksFilter filter) {
    return bookRepository.findAllBooksByFilter(filter).stream()
        .map(bookDao -> {
          var imageUrl = photoService.getBookCoverPhoto(bookDao.getCoverPhoto());
          return BookMapper.toEntity(bookDao, imageUrl);
        }).toList();
  }

  public void deleteBook(String id) {
    var dao = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    deleteExistingCoverPhoto(dao);
    removeBookFromOwner(dao);
    bookRepository.deleteById(id);
  }

  private void addBookToOwner(BookDao dao) {
    var owner = userRepository.findById(dao.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(dao.getOwner().getId()));
    owner.setBooks(Optional.ofNullable(owner.getBooks()).orElseGet(ArrayList::new));
    owner.getBooks().add(dao);
    userRepository.save(owner);
  }

  private void setOwnerToBook(Book book, BookDao bookDao) {
    var owner = userRepository.findById(book.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(book.getOwner().getId()));
    bookDao.setOwner(owner);
  }

  private void addGenresToBook(Book book, BookDao dao) {
    dao.setGenres(book.getGenres().stream()
        .map(genreName -> genreRepository.findByName(genreName)
            .orElseThrow(() -> new GenreNotFoundException(genreName)))
        .toList());
  }

  private BookDao addCoverPhotoToBook(Book book, BookDao dao) {
    String uniqueId = photoService.addBookCoverPhoto(book.getCoverPhotoFile(), dao.getId());
    dao.setCoverPhoto(uniqueId);
    return bookRepository.save(dao);
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
}