/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

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
      entity.setFavGenres(dao.getFavGenres().stream().map(GenreMapper::toEntity).toList());
    }
    entity.setProfilePhoto(dao.getProfilePhoto() != null ? dao.getProfilePhoto() : null);
    entity.setCoverPhoto(dao.getCoverPhoto() != null ? dao.getCoverPhoto() : null);
    entity.setBooks(dao.getBooks() != null ? dao.getBooks().stream().map(BookMapper::toEntity).toList() : null);
    entity
        .setFavBooks(dao.getFavBooks() != null ? dao.getFavBooks().stream().map(BookMapper::toEntity).toList() : null);
    return entity;
  }

  // Used only to create a new user
  public static UserDao toDao(User entity, String salt) {
    var dao = new UserDao();
    dao.setFirstName(entity.getFirstName());
    dao.setLastName(entity.getLastName());
    dao.setEmail(entity.getEmail());
    dao.setPassword(entity.getPassword());
    dao.setSalt(salt);
    return dao;
  }

  public static UserDao toDao(User entity) {
    var dao = new UserDao();
    dao.setId(entity.getId());
    dao.setFirstName(entity.getFirstName());
    dao.setLastName(entity.getLastName());
    dao.setEmail(entity.getEmail());
    dao.setPassword(entity.getPassword());
    dao.setStreetName(entity.getStreetName());
    dao.setHouseNumber(entity.getHouseNumber());
    dao.setZipCode(entity.getZipCode());
    dao.setCity(entity.getCity());
    dao.setCountry(entity.getCountry());
    dao.setPhoneNumber(entity.getPhoneNumber());
    dao.setAboutMe(entity.getAboutMe());
    if (entity.getFavGenres() != null) {
      dao.setFavGenres(entity.getFavGenres().stream().map(GenreMapper::toDao).toList());
    }
    dao.setProfilePhoto(entity.getProfilePhoto() != null ? entity.getProfilePhoto() : null);
    dao.setCoverPhoto(entity.getCoverPhoto() != null ? entity.getCoverPhoto() : null);
    dao.setBooks(entity.getBooks() != null ? entity.getBooks().stream().map(BookMapper::toDao).toList() : null);
    dao.setFavBooks(
        entity.getFavBooks() != null ? entity.getFavBooks().stream().map(BookMapper::toDao).toList() : null);
    return dao;
  }
}
