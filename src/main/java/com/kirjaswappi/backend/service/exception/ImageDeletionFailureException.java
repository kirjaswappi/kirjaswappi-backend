/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.exception;

import com.kirjaswappi.backend.common.exception.SystemException;

public class ImageDeletionFailureException extends SystemException {
  public ImageDeletionFailureException(String message) {
    super(message);
  }

  @Override
  public String getCode() {
    return "imageDeletionFailed";
  }
}
