/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.enums.Condition;
import com.kirjaswappi.backend.service.enums.Language;

@Component
@NoArgsConstructor
public class BookMapper {
  public static Book toEntity(BookDao dao) {
    var entity = new Book();
    entity.setId(dao.getId());
    entity.setTitle(dao.getTitle());
    entity.setAuthor(dao.getAuthor());
    entity.setDescription(dao.getDescription());
    entity.setLanguage(Language.fromCode(dao.getLanguage()));
    entity.setCondition(Condition.fromCode(dao.getCondition()));
    entity.setGenres(dao.getGenres().stream().map(GenreMapper::toEntity).toList());
    if (dao.getCoverPhoto() != null) {
      entity.setCoverPhoto(dao.getCoverPhoto());
    }
    entity.setSwapCondition(SwapConditionMapper.toEntity(dao.getSwapCondition()));
    return entity;
  }

  public static Book toEntity(BookDao dao, String imageUrl) {
    var entity = new Book();
    entity.setId(dao.getId());
    entity.setTitle(dao.getTitle());
    entity.setAuthor(dao.getAuthor());
    entity.setDescription(dao.getDescription());
    entity.setLanguage(Language.fromCode(dao.getLanguage()));
    entity.setCondition(Condition.fromCode(dao.getCondition()));
    entity.setGenres(dao.getGenres().stream().map(GenreMapper::toEntity).toList());
    entity.setCoverPhoto(imageUrl);
    entity.setSwapCondition(SwapConditionMapper.toEntity(dao.getSwapCondition()));
    return entity;
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
    dao.setLanguage(entity.getLanguage().getCode());
    dao.setCondition(entity.getCondition().getCode());
    dao.setSwapCondition(SwapConditionMapper.toDao(entity.getSwapCondition()));
    return dao;
  }
}
