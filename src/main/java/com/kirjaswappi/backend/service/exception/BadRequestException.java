/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.exception;

import com.kirjaswappi.backend.common.exception.BusinessException;

public class BadRequestException extends BusinessException {
  public BadRequestException(String messageKey, Object... params) {
    super(messageKey, params);
  }
}
