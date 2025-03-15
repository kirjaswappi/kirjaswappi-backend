/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.daos;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "exchange_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeConditionDao {
  @Id
  private String bookId;

  private boolean openForOffers;

  @NotNull
  @DBRef
  private List<GenreDao> exchangeableGenres;

  @NotNull
  @DBRef
  private List<ExchangeableBookDao> exchangeableBooks;
}
