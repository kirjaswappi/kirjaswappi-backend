/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.jpa.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kirjaswappi.backend.common.jpa.dao.AdminUserDao;

public interface AdminUserRepository extends MongoRepository<AdminUserDao, String> {
  Optional<AdminUserDao> findByUsername(String username);

  Optional<AdminUserDao> findByUsernameAndPassword(String username, String password);

  void deleteByUsername(String username);
}