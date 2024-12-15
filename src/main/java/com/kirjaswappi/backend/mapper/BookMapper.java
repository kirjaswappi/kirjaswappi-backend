/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.jpa.daos.GenreDao;
import com.kirjaswappi.backend.jpa.repositories.GenreRepository;
import com.kirjaswappi.backend.service.entities.Book;

@Component
@NoArgsConstructor
public class BookMapper {
  @Autowired
  private GenreRepository genreRepository;

  public static Book toEntity(BookDao dao) {
    var book = new Book();
    book.setId(dao.getId());
    book.setTitle(dao.getTitle());
    book.setAuthor(dao.getAuthor());
    book.setDescription(dao.getDescription());
    book.setLanguage(dao.getLanguage());
    book.setCondition(dao.getCondition());
    book.setGenres(dao.getGenres().stream().map(GenreDao::getName).toList());
    if (dao.getCoverPhoto() != null) {
      book.setCoverPhoto(PhotoMapper.toEntity(dao.getCoverPhoto()));
    }
    return book;
  }

  // This is without the genres
  public static BookDao toDao(Book entity) {
    var dao = new BookDao();
    dao.setId(entity.getId());
    dao.setTitle(entity.getTitle());
    dao.setAuthor(entity.getAuthor());
    dao.setDescription(entity.getDescription());
    dao.setLanguage(entity.getLanguage());
    dao.setCondition(entity.getCondition());
    if (entity.getCoverPhoto() != null) {
      dao.setCoverPhoto(PhotoMapper.toDao(entity.getCoverPhoto()));
    }
    return dao;
  }
}
