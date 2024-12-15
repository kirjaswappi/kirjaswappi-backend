/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class CreatePhotoRequest {
  private String userId;
  private MultipartFile image;

  public CreatePhotoRequest(String userId, MultipartFile image) {
    this.validateProperties();
    this.userId = userId;
    this.image = image;
  }

  private void validateProperties() {
    // validate email:
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
