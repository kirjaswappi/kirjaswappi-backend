/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.daos;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "exchange_books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeableBookDao {
  @Id
  private String id;

  @NotNull
  private String title;

  @NotNull
  private String author;

  @NotNull
  private String coverPhoto;
}
