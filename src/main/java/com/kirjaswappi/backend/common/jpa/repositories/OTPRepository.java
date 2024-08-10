/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.jpa.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kirjaswappi.backend.common.jpa.daos.OTPDao;

public interface OTPRepository extends MongoRepository<OTPDao, String> {
  Optional<OTPDao> findByEmail(String email);

  void deleteAllByEmail(String email);
}
