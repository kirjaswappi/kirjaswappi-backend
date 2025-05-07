/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.exceptions;

public class IllegalSwapRequestException extends BadRequestException {
  public IllegalSwapRequestException(String messageKey, Object... params) {
    super(messageKey, params);
  }
}
