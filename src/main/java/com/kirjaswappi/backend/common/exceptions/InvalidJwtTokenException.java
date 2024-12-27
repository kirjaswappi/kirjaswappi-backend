/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.exceptions;

public class InvalidJwtTokenException extends BusinessException {
  public InvalidJwtTokenException(Object... params) {
    super("invalidJwtToken", params);
  }
}
