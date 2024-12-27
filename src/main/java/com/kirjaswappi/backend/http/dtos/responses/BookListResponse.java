/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.responses;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entities.Book;

@Getter
@Setter
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
    this.genres = entity.getGenres();
    this.language = entity.getLanguage().name();
    this.description = entity.getDescription();
    this.condition = entity.getCondition().name();
    this.coverPhotoUrl = entity.getCoverPhoto() != null ? entity.getCoverPhoto() : null;
  }
}
