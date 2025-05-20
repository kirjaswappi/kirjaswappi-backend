/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.responses;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entities.Genre;

@Getter
@Setter
public class GenreResponse {
  private String id;
  private String name;
  private GenreResponse parentGenre;

  public GenreResponse(Genre entity) {
    this.id = entity.getId();
    this.name = entity.getName();
    if (entity.getParent() == null)
      this.parentGenre = null;
    else
      this.parentGenre = new GenreResponse(entity.getParent());
  }
}
