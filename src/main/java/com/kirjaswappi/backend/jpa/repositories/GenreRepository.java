/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kirjaswappi.backend.jpa.daos.GenreDao;

public interface GenreRepository extends MongoRepository<GenreDao, String> {
  boolean existsByName(String name);

  Optional<GenreDao> findByName(String name);
}
