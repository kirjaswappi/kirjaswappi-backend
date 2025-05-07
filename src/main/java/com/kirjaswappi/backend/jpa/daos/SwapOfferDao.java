/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.daos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.mongodb.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapOfferDao {
  @Nullable
  @DBRef
  private SwappableBookDao offeredBook;

  @Nullable
  @DBRef
  private GenreDao offeredGenre;
}
