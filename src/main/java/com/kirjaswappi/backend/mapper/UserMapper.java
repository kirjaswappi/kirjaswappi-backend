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
  public User toEntity(UserDao dao) {
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
    return entity;
  }

  public UserDao toDao(User entity) {
    var dao = new UserDao();
    dao.setId(entity.getId());
    dao.setFirstName(entity.getFirstName());
    dao.setLastName(entity.getLastName());
    dao.setStreetName(entity.getStreetName());
    dao.setHouseNumber(entity.getHouseNumber());
    dao.setZipCode(entity.getZipCode());
    dao.setCity(entity.getCity());
    dao.setCountry(entity.getCountry());
    dao.setPhoneNumber(entity.getPhoneNumber());
    return dao;
  }

  // This method is used to create a new user dao with salt
  public UserDao toDao(User entity, String salt) {
    var dao = new UserDao();
    dao.setId(entity.getId());
    dao.setFirstName(entity.getFirstName());
    dao.setLastName(entity.getLastName());
    dao.setEmail(entity.getEmail());
    dao.setPassword(entity.getPassword());
    dao.setSalt(salt);
    dao.setStreetName(entity.getStreetName());
    dao.setHouseNumber(entity.getHouseNumber());
    dao.setZipCode(entity.getZipCode());
    dao.setCity(entity.getCity());
    dao.setCountry(entity.getCountry());
    dao.setPhoneNumber(entity.getPhoneNumber());
    return dao;
  }

}
