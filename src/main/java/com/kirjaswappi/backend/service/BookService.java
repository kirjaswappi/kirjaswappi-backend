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
import com.kirjaswappi.backend.service.exceptions.ResourceNotFoundException;
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
    addGenresToBook(book, bookDao);
    setOwnerToBook(book, bookDao);
    addCoverPhotoToBook(book, bookDao);
    var savedDao = bookRepository.save(bookDao);
    addBookToOwner(savedDao);
    return BookMapper.setOwner(savedDao.getOwner(), book);
  }

  public Book updateBook(Book book) {
    var dao = BookMapper.toDao(book);
    setOwnerToBook(book, dao);
    addGenresToBook(book, dao);
    addCoverPhotoToBook(book, dao);
    var updatedBookDao = bookRepository.save(dao);
    addBookToOwner(updatedBookDao);
    return BookMapper.setOwner(updatedBookDao.getOwner(), book);
  }

  public Book getBookById(String id) throws Exception {
    var bookDao = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
    var book = BookMapper.toEntity(bookDao, photoService.getBookCoverById(id));
    return BookMapper.setOwner(bookDao.getOwner(), book);
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll().stream().map(
        bookDao -> {
          try {
            var imageUrl = photoService.getBookCoverById(bookDao.getId());
            return BookMapper.toEntity(bookDao, imageUrl);
          } catch (Exception e) {
            throw new ResourceNotFoundException("Book cover photo not found", bookDao.getId());
          }
        }).toList();
  }

  public List<Book> getAllBooksByFilter(GetAllBooksFilter filter) {
    return bookRepository.findAllBooksByFilter(filter).stream().map(
        bookDao -> {
          try {
            var imageUrl = photoService.getBookCoverById(bookDao.getId());
            return BookMapper.toEntity(bookDao, imageUrl);
          } catch (Exception e) {
            throw new ResourceNotFoundException("Book cover photo not found", bookDao.getId());
          }
        }).toList();
  }

  public void deleteBook(String id) {
    var dao = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    deleteExistingCoverPhoto(dao);
    removeBookFromOwner(dao);
    bookRepository.deleteById(id);
  }

  private void addBookToOwner(BookDao savedDao) {
    var owner = userRepository.findById(savedDao.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(savedDao.getOwner().getId()));
    owner.setBooks(Optional.ofNullable(owner.getBooks()).orElseGet(ArrayList::new));
    owner.getBooks().add(savedDao);
    userRepository.save(owner);
  }

  private void setOwnerToBook(Book book, BookDao bookDao) {
    var owner = userRepository.findById(book.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(book.getOwner().getId()));
    bookDao.setOwner(owner);
  }

  private void addGenresToBook(Book book, BookDao bookDao) {
    bookDao.setGenres(book.getGenres().stream()
        .map(genreName -> genreRepository.findByName(genreName)
            .orElseThrow(() -> new GenreNotFoundException(genreName)))
        .toList());
  }

  private void addCoverPhotoToBook(Book book, BookDao dao) {
    String coverPhoto = photoService.addBookCoverPhoto(book.getCoverPhotoFile());
    dao.setCoverPhoto(coverPhoto);
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