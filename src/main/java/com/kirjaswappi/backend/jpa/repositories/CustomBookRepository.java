/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import com.kirjaswappi.backend.jpa.daos.BookDao;

public interface CustomBookRepository {
  Page<BookDao> findAllBooksByFilter(Criteria criteria, Pageable pageable);

  void deleteLogically(String bookId);
}
