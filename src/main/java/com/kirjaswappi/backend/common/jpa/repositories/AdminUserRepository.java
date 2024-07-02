/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.jpa.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kirjaswappi.backend.common.jpa.daos.AdminUserDao;

public interface AdminUserRepository extends MongoRepository<AdminUserDao, String> {
  AdminUserDao findByUsername(String username);

  boolean existsByUsernameAndPassword(String username, String password);
}
