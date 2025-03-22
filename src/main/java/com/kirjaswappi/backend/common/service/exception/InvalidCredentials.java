/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service.exception;

import com.kirjaswappi.backend.common.exception.BusinessException;

public class InvalidCredentials extends BusinessException {
  public InvalidCredentials(Object... params) {
    super("invalidCredentials", params);
  }
}