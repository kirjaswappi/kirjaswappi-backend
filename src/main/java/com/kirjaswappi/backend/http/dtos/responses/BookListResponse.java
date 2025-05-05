/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.responses;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.hateoas.server.core.Relation;

import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.Genre;

@Getter
@Setter
@Relation(collectionRelation = "books")
public class BookListResponse {
  private final String id;
  private final String title;
  private final String author;
  private List<String> genres;
  private String language;
  private String description;
  private String condition;
  private String coverPhotoUrl;

  public BookListResponse(Book entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.author = entity.getAuthor();
    this.genres = entity.getGenres().stream().map(Genre::getName).toList();
    this.language = entity.getLanguage().getCode();
    this.description = entity.getDescription();
    this.condition = entity.getCondition().getCode();
    this.coverPhotoUrl = entity.getCoverPhotos() != null ? entity.getCoverPhotos().get(0) : null;
  }
}
