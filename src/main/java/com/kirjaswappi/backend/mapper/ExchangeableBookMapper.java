/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import java.util.Objects;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.ExchangeableBookDao;
import com.kirjaswappi.backend.service.entities.ExchangeableBook;

@Component
@NoArgsConstructor
public class ExchangeableBookMapper {
  public static ExchangeableBook toEntity(ExchangeableBookDao dao) {
    var entity = new ExchangeableBook();
    entity.setId(dao.getId());
    entity.setTitle(dao.getTitle());
    entity.setAuthor(dao.getAuthor());
    if (dao.getCoverPhoto() != null) {
      entity.setCoverPhoto(dao.getCoverPhoto());
    }
    return entity;
  }

  public static ExchangeableBook toEntity(ExchangeableBookDao dao, String imageUrl) {
    Objects.requireNonNull(imageUrl);
    var entity = new ExchangeableBook();
    entity.setId(dao.getId());
    entity.setTitle(dao.getTitle());
    entity.setAuthor(dao.getAuthor());
    entity.setCoverPhoto(imageUrl);
    return entity;
  }

  public static ExchangeableBookDao toDao(ExchangeableBook entity) {
    var dao = new ExchangeableBookDao();
    if (entity.getId() != null) {
      dao.setId(entity.getId());
    }
    dao.setTitle(entity.getTitle());
    dao.setAuthor(entity.getAuthor());
    if (entity.getCoverPhoto() != null) {
      dao.setCoverPhoto(entity.getCoverPhoto());
    }
    return dao;
  }

  public static ExchangeableBookDao toDao(ExchangeableBookDao dao, String imageUrl) {
    Objects.requireNonNull(imageUrl);
    dao.setCoverPhoto(imageUrl);
    return dao;
  }
}
