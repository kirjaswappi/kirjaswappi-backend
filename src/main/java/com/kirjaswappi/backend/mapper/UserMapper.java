/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.GenreDao;
import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.service.entities.User;

@Component
@NoArgsConstructor
public class UserMapper {
  public static User toEntity(UserDao dao) {
    var entity = new User();
    entity.setId(dao.getId());
    entity.setFirstName(dao.getFirstName());
    entity.setLastName(dao.getLastName());
    entity.setEmail(dao.getEmail());
    entity.setPassword(dao.getPassword());
    entity.setStreetName(dao.getStreetName());
    entity.setHouseNumber(dao.getHouseNumber());
    entity.setZipCode(dao.getZipCode());
    entity.setCity(dao.getCity());
    entity.setCountry(dao.getCountry());
    entity.setPhoneNumber(dao.getPhoneNumber());
    entity.setAboutMe(dao.getAboutMe());
    if (dao.getFavGenres() != null) {
      entity.setFavGenres(dao.getFavGenres().stream().map(GenreDao::getName).toList());
    }
    entity.setProfilePhoto(dao.getProfilePhoto() != null ? dao.getProfilePhoto() : null);
    entity.setCoverPhoto(dao.getCoverPhoto() != null ? dao.getCoverPhoto() : null);
    entity.setBooks(dao.getBooks() != null ? dao.getBooks().stream().map(BookMapper::toEntity).toList() : null);
    return entity;
  }

  // This method is used to create a new user
  public static UserDao toDao(User entity, String salt) {
    var dao = new UserDao();
    dao.setFirstName(entity.getFirstName());
    dao.setLastName(entity.getLastName());
    dao.setEmail(entity.getEmail());
    dao.setPassword(entity.getPassword());
    dao.setSalt(salt);
    return dao;
  }
}
