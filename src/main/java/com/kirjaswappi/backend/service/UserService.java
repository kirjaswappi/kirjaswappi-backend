/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.UserMapper;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.ResourceNotFound;

@Service
public class UserService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  UserMapper mapper;

  public User addUser(User user) {
    UserDao dao = mapper.toDao(user);
    return mapper.toEntity(userRepository.save(dao));
  }

  public User getUser(String id) {
    Optional<UserDao> dao = userRepository.findById(id);
    return mapper.toEntity(dao
        .orElseThrow(() -> new ResourceNotFound("userNotFound", id)));
  }

  public List<User> getUsers() {
    return userRepository.findAll().stream().map(mapper::toEntity).toList();
  }

  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }

  public User updateUser(User user) {
    UserDao dao = mapper.toDao(user);
    return mapper.toEntity(userRepository.save(dao));
  }
}
