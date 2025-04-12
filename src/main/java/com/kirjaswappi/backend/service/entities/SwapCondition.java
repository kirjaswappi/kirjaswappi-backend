/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.entities;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.kirjaswappi.backend.service.enums.SwapConditionType;

@Getter
@Setter
@NoArgsConstructor
public class SwapCondition {
  private SwapConditionType conditionType;
  private boolean giveAway;
  private boolean openForOffers;
  private List<Genre> swappableGenres;
  private List<SwappableBook> swappableBooks;

  public SwapCondition(SwapConditionType conditionType,
      boolean giveAway,
      boolean openForOffers,
      List<Genre> swappableGenres,
      List<SwappableBook> swappableBooks) {
    this.conditionType = conditionType;
    this.giveAway = giveAway;
    this.openForOffers = openForOffers;
    this.swappableGenres = swappableGenres != null ? swappableGenres : List.of();
    this.swappableBooks = swappableBooks != null ? swappableBooks : List.of();
  }
}
