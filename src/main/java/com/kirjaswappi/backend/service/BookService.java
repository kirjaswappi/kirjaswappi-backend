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
import com.kirjaswappi.backend.service.entities.Photo;
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
    // convert book to dao without genres, owner and cover photo:
    var bookDao = BookMapper.toDao(book);
    // add genres to book:
    addGenresToTheBook(book, bookDao);
    // set the owner of the book:
    addOwnerToTheBook(book, bookDao);
    // add cover photo to the book:
    var photo = addCoverPhotoToTheBook(book, bookDao);
    var savedDao = bookRepository.save(bookDao);
    // finally, add book to the owner's book list:
    addBookToOwner(savedDao);
    var savedBook = BookMapper.toEntity(savedDao);
    // set photoBytes to the book:
    assert savedBook.getCoverPhoto() != null;
    savedBook.getCoverPhoto().setFileBytes(photo.getFileBytes());
    return BookMapper.setOwner(savedDao.getOwner(), savedBook);
  }

  private void addBookToOwner(BookDao savedDao) {
    var owner = userRepository.findById(savedDao.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(savedDao.getOwner().getId()));
    owner.setBooks(Optional.ofNullable(owner.getBooks()).orElseGet(ArrayList::new));
    owner.getBooks().add(savedDao);
    userRepository.save(owner);
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

  private Photo addCoverPhotoToTheBook(Book book, BookDao dao) throws IOException {
    deleteExistingCoverPhoto(dao);
    assert book.getCoverPhoto() != null;
    var photo = photoService.addBookCoverPhoto(book.getCoverPhoto().getFile());
    dao.setCoverPhoto(PhotoMapper.toDao(photo));
    return photo;
  }

  private void deleteExistingCoverPhoto(BookDao dao) {
    // This ensures we don't have dangling cover photos for a book
    if (dao.getCoverPhoto() != null) {
      photoService.deleteBookCoverPhoto(dao.getCoverPhoto());
    }
  }

  public Book getBookById(String id) {
    var bookDao = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
    var book = BookMapper.toEntity(bookDao, photoService.getBookCoverById(id));
    return BookMapper.setOwner(bookDao.getOwner(), book);
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
    var photo = addCoverPhotoToTheBook(book, dao);
    var updatedBookDao = bookRepository.save(dao);
    addBookToOwner(updatedBookDao);
    var updatedBook = BookMapper.toEntity(updatedBookDao);
    assert updatedBook.getCoverPhoto() != null;
    updatedBook.getCoverPhoto().setFileBytes(photo.getFileBytes());
    return BookMapper.setOwner(updatedBookDao.getOwner(), updatedBook);
  }

  private void findAndSetOwnerToTheBook(Book book, BookDao dao) {
    var owner = bookRepository.findById(book.getId())
        .orElseThrow(() -> new BookNotFoundException(book.getId())).getOwner();
    dao.setOwner(owner);
  }

  public void deleteBook(String id) {
    var dao = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    deleteExistingCoverPhoto(dao);
    findOwnerAndRemoveBookFromOwner(dao);
    bookRepository.deleteById(id);
  }

  private void findOwnerAndRemoveBookFromOwner(BookDao dao) {
    var owner = userRepository.findById(dao.getOwner().getId())
        .orElseThrow(() -> new UserNotFoundException(dao.getOwner().getId()));
    if (owner.getBooks() == null) {
      return;
    }
    owner.setBooks(owner.getBooks().stream()
        .filter(book -> !book.getId().equals(dao.getId()))
        .toList());
    userRepository.save(owner);
  }

  public List<Book> getAllBooksByFilter(GetAllBooksFilter filter) {
    return bookRepository.findAllBooksByFilter(filter).stream().map(
        bookDao -> BookMapper.toEntity(bookDao, photoService.getBookCoverById(bookDao.getId()))).toList();
  }
}
