/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import java.util.Objects;
import java.util.UUID;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.SwappableBookDao;
import com.kirjaswappi.backend.service.entities.SwappableBook;

@Component
@NoArgsConstructor
public class SwappableBookMapper {
  public static SwappableBook toEntity(SwappableBookDao dao) {
    var entity = new SwappableBook();
    entity.setId(dao.getId());
    entity.setTitle(dao.getTitle());
    entity.setAuthor(dao.getAuthor());
    if (dao.getCoverPhoto() != null) {
      entity.setCoverPhoto(dao.getCoverPhoto());
    }
    return entity;
  }

  public static SwappableBook toEntity(SwappableBookDao dao, String imageUrl) {
    Objects.requireNonNull(imageUrl);
    var entity = new SwappableBook();
    entity.setId(dao.getId());
    entity.setTitle(dao.getTitle());
    entity.setAuthor(dao.getAuthor());
    entity.setCoverPhoto(imageUrl);
    return entity;
  }

  public static SwappableBookDao toDao(SwappableBook entity) {
    var dao = new SwappableBookDao();
    if (entity.getId() != null) {
      dao.setId(entity.getId());
    } else
      dao.setId(UUID.randomUUID().toString());
    dao.setTitle(entity.getTitle());
    dao.setAuthor(entity.getAuthor());
    if (entity.getCoverPhoto() != null) {
      dao.setCoverPhoto(entity.getCoverPhoto());
    }
    return dao;
  }

  public static SwappableBookDao toDao(SwappableBookDao dao, String imageUrl) {
    Objects.requireNonNull(imageUrl);
    dao.setCoverPhoto(imageUrl);
    return dao;
  }
}
