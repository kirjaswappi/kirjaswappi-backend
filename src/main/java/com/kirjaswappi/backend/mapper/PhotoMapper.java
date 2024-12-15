/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
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
  public static Photo toEntity(PhotoDao dao) {
    return new Photo(dao.getId(), dao.getTitle(), dao.getFileId());
  }

  public static PhotoDao toDao(Photo entity) {
    return new PhotoDao(entity.getId(), entity.getTitle(), entity.getFileId());
  }
}
