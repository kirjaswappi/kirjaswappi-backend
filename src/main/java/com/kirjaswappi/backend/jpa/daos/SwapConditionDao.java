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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapConditionDao {
  @NotNull
  private String conditionType;

  @NotNull
  private boolean giveAway;

  @NotNull
  private boolean openForOffers;

  @NotNull
  private List<GenreDao> swappableGenres;

  @NotNull
  private List<SwappableBookDao> swappableBooks;
}
