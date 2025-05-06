/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.exceptions;

import com.kirjaswappi.backend.common.exceptions.SystemException;

public class ImageDeletionFailureException extends SystemException {
  private final Object[] params;

  public ImageDeletionFailureException(Object... params) {
    super("imageDeletionFailed");
    this.params = params;
  }

  @Override
  public String getCode() {
    return "imageDeletionFailed";
  }

  @Override
  public Object[] getParams() {
    return params;
  }
}
