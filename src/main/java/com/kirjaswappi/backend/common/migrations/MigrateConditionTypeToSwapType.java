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

@ChangeUnit(id = "migrateConditionTypeToSwapType", order = "0002", author = "mahiuddinalkamal")
public class MigrateConditionTypeToSwapType {

  private final MongoTemplate mongoTemplate;

  public MigrateConditionTypeToSwapType(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Execution
  public void executeMigration() {
    // Find all books where swapCondition.conditionType exists
    Query query = new Query(Criteria.where("swapCondition.conditionType").exists(true));
    var books = mongoTemplate.find(query, Document.class, "books");

    for (var book : books) {
      Document swapCondition = (Document) book.get("swapCondition");
      if (swapCondition != null && swapCondition.containsKey("conditionType")) {
        Object conditionTypeValue = swapCondition.get("conditionType");

        // Remove old field and set new one
        Update update = new Update()
            .unset("swapCondition.conditionType")
            .set("swapCondition.swapType", conditionTypeValue);

        mongoTemplate.updateFirst(
            new Query(Criteria.where("_id").is(book.getObjectId("_id"))),
            update,
            "books");
      }
    }
  }

  @RollbackExecution
  public void rollback() {
    // Optional: reverse the rename
    Query query = new Query(Criteria.where("swapCondition.swapType").exists(true));
    var books = mongoTemplate.find(query, Document.class, "books");

    for (var book : books) {
      Document swapCondition = (Document) book.get("swapCondition");
      if (swapCondition != null && swapCondition.containsKey("swapType")) {
        Object swapTypeValue = swapCondition.get("swapType");

        Update update = new Update()
            .unset("swapCondition.swapType")
            .set("swapCondition.conditionType", swapTypeValue);

        mongoTemplate.updateFirst(
            new Query(Criteria.where("_id").is(book.getObjectId("_id"))),
            update,
            "books");
      }
    }
  }
}
