/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.io.IOException;
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
import com.kirjaswappi.backend.mapper.PhotoMapper;
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

  public Book createBook(Book book) throws IOException {
    var bookDao = BookMapper.toDao(book);
    addGenresToBook(book, bookDao);
    setOwnerToBook(book, bookDao);
    var photoBytes = addCoverPhotoToBook(book, bookDao);
    var savedDao = bookRepository.save(bookDao);
    addBookToOwner(savedDao);
    return buildBookWithPhoto(savedDao, photoBytes);
  }

  public Book updateBook(Book book) throws IOException {
    var dao = BookMapper.toDao(book);
    setOwnerToBook(book, dao);
    addGenresToBook(book, dao);
    var photoBytes = addCoverPhotoToBook(book, dao);
    var updatedBookDao = bookRepository.save(dao);
    addBookToOwner(updatedBookDao);
    return buildBookWithPhoto(updatedBookDao, photoBytes);
  }

  public Book getBookById(String id) {
    var bookDao = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
    var book = BookMapper.toEntity(bookDao, photoService.getBookCoverById(id));
    return BookMapper.setOwner(bookDao.getOwner(), book);
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll().stream().map(
        bookDao -> BookMapper.toEntity(bookDao, photoService.getBookCoverById(bookDao.getId()))).toList();
  }

  public List<Book> getAllBooksByFilter(GetAllBooksFilter filter) {
    return bookRepository.findAllBooksByFilter(filter).stream().map(
        bookDao -> BookMapper.toEntity(bookDao, photoService.getBookCoverById(bookDao.getId()))).toList();
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

  private byte[] addCoverPhotoToBook(Book book, BookDao dao) throws IOException {
    deleteExistingCoverPhoto(dao);
    assert book.getCoverPhoto() != null;
    var photo = photoService.addBookCoverPhoto(book.getCoverPhoto().getFile());
    dao.setCoverPhoto(PhotoMapper.toDao(photo));
    return photo.getFileBytes();
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

  private Book buildBookWithPhoto(BookDao bookDao, byte[] photoBytes) {
    var book = BookMapper.toEntity(bookDao);
    assert book.getCoverPhoto() != null;
    book.getCoverPhoto().setFileBytes(photoBytes);
    return BookMapper.setOwner(bookDao.getOwner(), book);
  }
}