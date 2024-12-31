/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.kirjaswappi.backend.jpa.daos.BookDao;

@Repository
public class CustomBookRepositoryImpl implements CustomBookRepository {

  private final MongoTemplate mongoTemplate;

  public CustomBookRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public Page<BookDao> findAllBooksByFilter(Query query, Pageable pageable) {
    long total = mongoTemplate.count(query, BookDao.class);
    query.with(pageable);
    List<BookDao> bookDaos = mongoTemplate.find(query, BookDao.class);
    return new PageImpl<>(bookDaos, pageable, total);
  }
}
