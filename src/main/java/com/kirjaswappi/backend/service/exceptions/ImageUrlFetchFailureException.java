/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.exceptions;

import com.kirjaswappi.backend.common.exceptions.SystemException;

public class ImageUrlFetchFailureException extends SystemException {
  public ImageUrlFetchFailureException(String message) {
    super(message);
  }

  @Override
  public String getCode() {
    return "imageUrlFetchFailed";
  }
}