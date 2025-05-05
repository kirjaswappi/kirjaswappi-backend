/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.PhotoDao;
import com.kirjaswappi.backend.service.entities.Photo;

@Component
@NoArgsConstructor
public class PhotoMapper {
  public static Photo toEntity(String id, String imageUrl) {
    var entity = new Photo();
    entity.setId(id);
    entity.setCoverPhoto(imageUrl);
    return entity;
  }

  public static PhotoDao toDao(Photo entity) {
    var dao = new PhotoDao();
    dao.setId(entity.getId());
    dao.setCoverPhoto(entity.getCoverPhoto());
    return dao;
  }
}
