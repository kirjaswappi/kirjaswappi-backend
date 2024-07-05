/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kirjaswappi.backend.common.service.exceptions.InvalidCredentials;
import com.kirjaswappi.backend.common.utils.Util;
import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.UserMapper;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequest;
import com.kirjaswappi.backend.service.exceptions.UserNotFound;

@Service
public class UserService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  UserMapper mapper;

  public User addUser(User user) {
    // validate email exists:
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new BadRequest("emailAlreadyExists", user.getEmail());
    }

    // add salt to password:
    String salt = Util.generateSalt();
    user.setPassword(user.getPassword(), salt);

    // save user:
    UserDao dao = mapper.toDao(user, salt);
    return mapper.toEntity(userRepository.save(dao));
  }

  public User getUser(String id) {
    return mapper.toEntity(userRepository.findById(id)
        .orElseThrow(() -> new UserNotFound("userNotFound", id)));
  }

  public List<User> getUsers() {
    return userRepository.findAll().stream().map(mapper::toEntity).toList();
  }

  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }

  public User updateUser(User user) {
    // validate user exists:
    if (userRepository.findById(user.getId()).isEmpty()) {
      throw new UserNotFound("userNotFound", user.getId());
    }
    UserDao dao = mapper.toDao(user);
    return mapper.toEntity(userRepository.save(dao));
  }

  public User verifyLogin(User user) {
    // get salt from email:
    UserDao dao = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new InvalidCredentials("invalidCredentials"));

    // hash password with salt:
    String password = Util.hashPassword(user.getPassword(), dao.getSalt());

    // validate email and password and return user:
    return mapper.toEntity(userRepository.findByEmailAndPassword(dao.getEmail(), password)
        .orElseThrow(() -> new InvalidCredentials("invalidCredentials")));
  }
}
