/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kirjaswappi.backend.jpa.daos.UserDao;

public interface UserRepository extends MongoRepository<UserDao, String> {
  // Custom queries if needed
}
