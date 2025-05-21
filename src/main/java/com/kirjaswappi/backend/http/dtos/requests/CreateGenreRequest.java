/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class CreateGenreRequest {
  @Schema(description = "The name of the genre.", example = "Fiction", requiredMode = REQUIRED)
  private String name;

  @Schema(description = "The ID of the parent genre.", example = "123456 or null", requiredMode = REQUIRED)
  private String parentId;

  public Genre toEntity() {
    this.validateProperties();
    var entity = new Genre();
    entity.setName(this.name);
    if (parentId != null && !parentId.trim().isEmpty()) {
      var parentGenre = new Genre();
      parentGenre.setId(parentId);
      entity.setParent(parentGenre);
    } else
      entity.setParent(null);
    return entity;
  }

  private void validateProperties() {
    // validate genre name:
    if (!ValidationUtil.validateNotBlank(this.name)) {
      throw new BadRequestException("genreNameCannotBeBlank", this.name);
    }
  }
}
