/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.responses;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.ExchangeCondition;
import com.kirjaswappi.backend.service.entities.Genre;
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
  private String coverPhotoUrl;
  private OwnerResponse owner;
  private ExchangeCondition exchangeCondition;

  public BookResponse(Book entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.author = entity.getAuthor();
    this.genres = entity.getGenres().stream().map(Genre::getName).toList();
    this.language = entity.getLanguage().getCode();
    this.description = entity.getDescription();
    this.condition = entity.getCondition().getCode();
    this.coverPhotoUrl = entity.getCoverPhoto() == null ? null : entity.getCoverPhoto();
    this.owner = new OwnerResponse(entity.getOwner());
    this.exchangeCondition = entity.getExchangeCondition();
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
