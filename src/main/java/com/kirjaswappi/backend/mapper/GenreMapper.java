/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.GenreDao;
import com.kirjaswappi.backend.service.entities.Genre;

@Component
@NoArgsConstructor
public class GenreMapper {
  public static Genre toEntity(GenreDao dao) {
    return new Genre(dao.getId(), dao.getName(),
        dao.getParent() == null ? null : toEntity(dao.getParent()));
  }

  public static GenreDao toDao(Genre entity) {
    return new GenreDao(entity.getId(), entity.getName(),
        entity.getParent() == null ? null : toDao(entity.getParent()));
  }
}
