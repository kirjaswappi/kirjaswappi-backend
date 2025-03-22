/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.exception;

/**
 * An error which is related to a {@link BusinessException} (or one of its
 * subclasses), to be used when several business errors need to be collected as
 * detail.
 */
public class Error {
  private final String messageKey;
  private final Object[] messageParams;

  public Error(String messageKey, Object... messageParams) {
    this.messageKey = messageKey;
    this.messageParams = messageParams;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public Object[] getMessageParams() {
    return this.messageParams;
  }
}
