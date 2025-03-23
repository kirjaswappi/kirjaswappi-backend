/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.exception;

public class GlobalSystemException extends SystemException {
  public GlobalSystemException(String message) {
    super(message);
  }

  @Override
  public String getCode() {
    return "GlobalSystemException";
  }
}
