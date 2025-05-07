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
public class CreateSupportedCoverPhotoRequest {
  @Schema(description = "The image file of the photo.", requiredMode = REQUIRED)
  private MultipartFile coverPhoto;

  public CreateSupportedCoverPhotoRequest(MultipartFile coverPhoto) {
    this.coverPhoto = coverPhoto;
    this.validateProperties();
  }

  private void validateProperties() {
    // validate image:
    if (this.coverPhoto == null) {
      throw new BadRequestException("imageCannotBeNull");
    }
    ValidationUtil.validateMediaType(this.coverPhoto);
  }
}
