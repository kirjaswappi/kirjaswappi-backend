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
import com.kirjaswappi.backend.service.entities.ExchangeableBook;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.entities.User;

@Getter
@Setter
public class BookResponse {
  private final String id;
  private final String title;
  private final String author;
  private List<String> genres;
  private final String language;
  private final String description;
  private final String condition;
  private final String coverPhotoUrl;
  private OwnerResponse owner;
  private ExchangeConditionResponse exchangeCondition;

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
    this.exchangeCondition = new ExchangeConditionResponse(entity.getExchangeCondition());
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

  @Setter
  @Getter
  static class ExchangeConditionResponse {
    private boolean openForOffers;
    private List<Genre> exchangeableGenres;
    private List<ExchangeableBookResponse> exchangeableBooks;

    public ExchangeConditionResponse(ExchangeCondition entity) {
      this.openForOffers = entity.isOpenForOffers();
      if (entity.getExchangeableGenres() != null) {
        this.exchangeableGenres = entity.getExchangeableGenres();
      }
      if (entity.getExchangeableBooks() != null) {
        this.exchangeableBooks = entity.getExchangeableBooks()
            .stream().map(ExchangeableBookResponse::new).toList();
      }
    }
  }

  @Setter
  @Getter
  static class ExchangeableBookResponse {
    private final String title;
    private final String author;
    private final String coverPhotoUrl;

    public ExchangeableBookResponse(ExchangeableBook entity) {
      this.title = entity.getTitle();
      this.author = entity.getAuthor();
      this.coverPhotoUrl = entity.getCoverPhoto();
    }
  }
}
