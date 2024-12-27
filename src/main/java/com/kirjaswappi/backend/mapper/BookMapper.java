/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.jpa.daos.GenreDao;
import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.enums.Condition;
import com.kirjaswappi.backend.service.enums.Language;

@Component
@NoArgsConstructor
public class BookMapper {
  public static Book toEntity(BookDao dao) {
    var book = new Book();
    book.setId(dao.getId());
    book.setTitle(dao.getTitle());
    book.setAuthor(dao.getAuthor());
    book.setDescription(dao.getDescription());
    book.setLanguage(Language.fromString(dao.getLanguage()));
    book.setCondition(Condition.fromString(dao.getCondition()));
    book.setGenres(dao.getGenres().stream().map(GenreDao::getName).toList());
    if (dao.getCoverPhoto() != null) {
      book.setCoverPhoto(dao.getCoverPhoto());
    }
    return book;
  }

  public static Book toEntity(BookDao dao, String imageUrl) {
    var book = new Book();
    book.setId(dao.getId());
    book.setTitle(dao.getTitle());
    book.setAuthor(dao.getAuthor());
    book.setDescription(dao.getDescription());
    book.setLanguage(Language.fromString(dao.getLanguage()));
    book.setCondition(Condition.fromString(dao.getCondition()));
    book.setGenres(dao.getGenres().stream().map(GenreDao::getName).toList());
    book.setCoverPhoto(imageUrl);
    return book;
  }

  public static Book setOwner(UserDao owner, Book book) {
    book.setOwner(UserMapper.toEntity(owner));
    return book;
  }

  // This is without the genres, cover photo and owner
  public static BookDao toDao(Book entity) {
    var dao = new BookDao();
    if (entity.getId() != null) {
      dao.setId(entity.getId());
    }
    dao.setTitle(entity.getTitle());
    dao.setAuthor(entity.getAuthor());
    dao.setDescription(entity.getDescription());
    dao.setLanguage(entity.getLanguage().name());
    dao.setCondition(entity.getCondition().name());
    return dao;
  }
}
