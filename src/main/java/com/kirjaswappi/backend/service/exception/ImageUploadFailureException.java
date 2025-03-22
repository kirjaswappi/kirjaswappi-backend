/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.exception;

import com.kirjaswappi.backend.common.exception.SystemException;

public class ImageUploadFailureException extends SystemException {
  public ImageUploadFailureException(String message) {
    super(message);
  }

  @Override
  public String getCode() {
    return "imageUploadFailed";
  }
}