/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class CreatePhotoRequest {
  @Schema(description = "The user ID of the photo owner.", example = "123456", requiredMode = REQUIRED)
  private String userId;

  @Schema(description = "The image file of the photo.", requiredMode = REQUIRED)
  private MultipartFile image;

  public CreatePhotoRequest(String userId, MultipartFile image) {
    this.userId = userId;
    this.image = image;
    this.validateProperties();
  }

  private void validateProperties() {
    if (!ValidationUtil.validateNotBlank(this.userId)) {
      throw new BadRequestException("userIdCannotBeBlank", this.userId);
    }
    // validate image:
    if (this.image == null) {
      throw new BadRequestException("imageCannotBeNull", this.image);
    }
    ValidationUtil.validateMediaType(this.image);
  }
}
