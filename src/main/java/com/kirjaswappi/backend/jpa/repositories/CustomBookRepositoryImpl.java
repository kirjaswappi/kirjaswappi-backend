/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.kirjaswappi.backend.common.exceptions.GlobalSystemException;
import com.kirjaswappi.backend.jpa.daos.BookDao;

@Repository
public class CustomBookRepositoryImpl implements CustomBookRepository {
  private static final Logger logger = LoggerFactory.getLogger(CustomBookRepositoryImpl.class);

  private final MongoTemplate mongoTemplate;

  public CustomBookRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public Page<BookDao> findAllBooksByFilter(Criteria criteria, Pageable pageable) {
    try {
      // Define the lookup operation to join BookDao with GenreDao
      LookupOperation lookupOperation = LookupOperation.newLookup()
          .from("genres")
          .localField("genres.$id")
          .foreignField("_id")
          .as("genres");

      // Define the match operation based on the query criteria
      MatchOperation matchOperation = Aggregation.match(criteria);

      // Define the aggregation pipeline
      Aggregation aggregation = Aggregation.newAggregation(
          lookupOperation,
          matchOperation,
          Aggregation.skip(pageable.getOffset()),
          Aggregation.limit(pageable.getPageSize()));

      // Execute the aggregation query
      List<BookDao> bookDaos = mongoTemplate.aggregate(aggregation, "books", BookDao.class).getMappedResults();
      long total = mongoTemplate.count(new Query(criteria), BookDao.class);

      return new PageImpl<>(bookDaos, pageable, total);
    } catch (Exception e) {
      logger.error("Error occurred while fetching books: " + e.getMessage());
      throw new GlobalSystemException("Error occurred while fetching books, please try again later");
    }
  }
}
