/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.entity;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeCondition {
  private boolean openForOffers;
  private List<Genre> exchangeableGenres;
  private List<ExchangeableBook> exchangeableBooks;

  public ExchangeCondition(boolean openForOffers,
      List<Genre> exchangeableGenres,
      List<ExchangeableBook> exchangeableBooks) {
    checkIfOnlyOneOfTheExchangeConditionIsProvided(openForOffers, exchangeableGenres, exchangeableBooks);
    this.openForOffers = openForOffers;
    this.exchangeableGenres = exchangeableGenres != null ? exchangeableGenres : List.of();
    this.exchangeableBooks = exchangeableBooks != null ? exchangeableBooks : List.of();
  }

  private static void checkIfOnlyOneOfTheExchangeConditionIsProvided(boolean openForOffers,
      List<Genre> exchangeableGenres, List<ExchangeableBook> exchangeableBooks) {
    boolean isGenresSet = exchangeableGenres != null && !exchangeableGenres.isEmpty();
    boolean isBooksSet = exchangeableBooks != null && !exchangeableBooks.isEmpty();

    if ((openForOffers && (isGenresSet || isBooksSet)) ||
        (!openForOffers && !isGenresSet && !isBooksSet)) {
      throw new IllegalArgumentException("Exactly one of the exchange conditions must be set");
    }
  }
}
