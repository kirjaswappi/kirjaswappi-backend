/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.migrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(id = "addParentFieldToGenres", order = "0003", author = "mahiuddinalkamal")
public class AddParentFieldToGenres {

  private final MongoTemplate mongoTemplate;

  public AddParentFieldToGenres(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Execution
  public void executeMigration() {
    // Find all genres that do not have the 'parent' field
    Query query = new Query(Criteria.where("parent").exists(false));
    var genres = mongoTemplate.find(query, Document.class, "genres");

    for (var genre : genres) {
      Update update = new Update().set("parent", null);

      mongoTemplate.updateFirst(
          new Query(Criteria.where("_id").is(genre.getObjectId("_id"))),
          update,
          "genres");
    }
  }

  @RollbackExecution
  public void rollback() {
    // Remove the 'parent' field from genres where it's null (i.e., added by this
    // migration)
    Query query = new Query(Criteria.where("parent").is(null));
    var genres = mongoTemplate.find(query, Document.class, "genres");

    for (var genre : genres) {
      Update update = new Update().unset("parent");

      mongoTemplate.updateFirst(
          new Query(Criteria.where("_id").is(genre.getObjectId("_id"))),
          update,
          "genres");
    }
  }
}
