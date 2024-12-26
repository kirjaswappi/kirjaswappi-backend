/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.responses;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.User;

@Getter
@Setter
public class BookResponse {
  private final String id;
  private final String title;
  private final String author;
  private List<String> genres;
  private String language;
  private String description;
  private String condition;
  private byte[] coverPhoto;
  private OwnerResponse owner;

  public BookResponse(Book entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.author = entity.getAuthor();
    this.genres = entity.getGenres();
    this.language = entity.getLanguage();
    this.description = entity.getDescription();
    this.condition = entity.getCondition();
    this.coverPhoto = entity.getCoverPhoto() == null ? null : entity.getCoverPhoto().getFileBytes();
    this.owner = new OwnerResponse(entity.getOwner());
  }

  @Setter
  @Getter
  static class OwnerResponse {
    private final String id;
    private final String name;

    public OwnerResponse(User entity) {
      this.id = entity.getId();
      this.name = entity.getFirstName() + " " + entity.getLastName();
    }
  }

}
