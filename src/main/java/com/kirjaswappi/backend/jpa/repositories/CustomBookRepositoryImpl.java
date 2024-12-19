/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.service.filters.GetAllBooksFilter;

@Repository
public class CustomBookRepositoryImpl implements CustomBookRepository {

  private final MongoTemplate mongoTemplate;

  public CustomBookRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public List<BookDao> findAllBooksByFilter(GetAllBooksFilter filter) {
    Query query = new Query();

    if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
      query.addCriteria(new Criteria().orOperator(
          Criteria.where("title").regex(filter.getSearch(), "i"),
          Criteria.where("author").regex(filter.getSearch(), "i"),
          Criteria.where("description").regex(filter.getSearch(), "i")));
    }

    if (filter.getLanguage() != null && !filter.getLanguage().isEmpty()) {
      query.addCriteria(Criteria.where("language").is(filter.getLanguage()));
    }

    if (filter.getCondition() != null && !filter.getCondition().isEmpty()) {
      query.addCriteria(Criteria.where("condition").is(filter.getCondition()));
    }

    if (filter.getGenre() != null && !filter.getGenre().isEmpty()) {
      query.addCriteria(Criteria.where("genres.name").is(filter.getGenre()));
    }

    return mongoTemplate.find(query, BookDao.class);
  }
}
