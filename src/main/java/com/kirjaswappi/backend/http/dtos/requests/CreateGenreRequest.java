/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class CreateGenreRequest {
  private String name;

  public Genre toEntity() {
    this.validateProperties();
    var entity = new Genre();
    entity.setName(this.name);
    return entity;
  }

  private void validateProperties() {
    // validate genre name:
    if (!ValidationUtil.validateNotBlank(this.name)) {
      throw new BadRequestException("genreNameCannotBeBlank", this.name);
    }
  }
}
