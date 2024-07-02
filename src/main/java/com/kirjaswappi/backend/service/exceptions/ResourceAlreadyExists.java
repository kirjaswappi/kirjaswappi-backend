/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.exceptions;

import com.kirjaswappi.backend.common.exceptions.BusinessException;

public class ResourceAlreadyExists extends BusinessException {
  public ResourceAlreadyExists(String messageKey) {
    super(messageKey);
  }

  public ResourceAlreadyExists(String messageKey, Object... params) {
    super(messageKey, params);
  }
}
