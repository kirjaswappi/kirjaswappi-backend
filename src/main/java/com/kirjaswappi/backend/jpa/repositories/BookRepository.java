/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kirjaswappi.backend.jpa.daos.BookDao;

public interface BookRepository extends MongoRepository<BookDao, String> {
}
