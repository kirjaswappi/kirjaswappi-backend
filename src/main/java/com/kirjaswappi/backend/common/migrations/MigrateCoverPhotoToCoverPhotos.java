/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.migrations;

import java.util.Set;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ChangeUnit(id = "migrateCoverPhotoToCoverPhotos", order = "0001", author = "mahiuddinalkamal")
public class MigrateCoverPhotoToCoverPhotos {

  private final MongoTemplate mongoTemplate;

  public MigrateCoverPhotoToCoverPhotos(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Execution
  public void executeMigration() {
    Query query = new Query(Criteria.where("coverPhoto").ne(null));
    var books = mongoTemplate.find(query, Document.class, "books");

    for (var book : books) {
      String oldCoverPhoto = book.getString("coverPhoto");
      if (oldCoverPhoto != null && !oldCoverPhoto.isBlank()) {
        Update update = new Update()
            .unset("coverPhoto")
            .set("coverPhotos", Set.of(oldCoverPhoto));

        mongoTemplate.updateFirst(
            new Query(Criteria.where("_id").is(book.getObjectId("_id"))),
            update,
            "books");
      }
    }
  }

  @RollbackExecution
  public void rollback() {
    // skip it
  }
}
