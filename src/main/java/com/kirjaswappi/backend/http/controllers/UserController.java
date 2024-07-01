/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.USERS;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.jpa.daos.User;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;

@RestController
@RequestMapping(API_BASE + USERS)
public class UserController {
  @Autowired
  private UserRepository userRepository;

  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User savedUser = userRepository.save(user);
    return ResponseEntity.created(URI.create(API_BASE + USERS)).body(savedUser);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserInfo(@PathVariable String id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    userRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
