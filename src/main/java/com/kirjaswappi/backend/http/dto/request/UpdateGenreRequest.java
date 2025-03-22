/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validation.ValidationUtil;
import com.kirjaswappi.backend.service.entity.Genre;
import com.kirjaswappi.backend.service.exception.BadRequestException;

@Getter
@Setter
public class UpdateGenreRequest {
  @Schema(description = "The genre id.", example = "1", requiredMode = REQUIRED)
  private String id;

  @Schema(description = "The genre name.", example = "Fiction", requiredMode = REQUIRED)
  private String name;

  public Genre toEntity() {
    this.validateProperties();
    var entity = new Genre();
    entity.setId(this.id);
    entity.setName(this.name);
    return entity;
  }

  private void validateProperties() {
    // validate genre id:
    if (!ValidationUtil.validateNotBlank(this.id)) {
      throw new BadRequestException("genreIdCannotBeBlank", this.id);
    }
    // validate genre name:
    if (!ValidationUtil.validateNotBlank(this.name)) {
      throw new BadRequestException("genreNameCannotBeBlank", this.name);
    }
  }
}
