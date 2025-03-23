/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.exception;

/**
 * Base exception class for all exceptions which are unexpected from business
 * perspective (e. g. IOExceptions, Connection Errors, ...)
 */
public abstract class SystemException extends RuntimeException {

  /**
   * Construct a SystemException just with a message. Please not, if the failure
   * situation is caused by another exception, use the constructor
   * {@link #SystemException(String, Throwable)} to avoid losing information.
   */
  protected SystemException(String message) {
    super(message);
  }

  /**
   * Construct a system exception with a message and cause.
   */
  protected SystemException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Return a code unique for this type of exception, e. g. "inconsistentData"
   */
  public abstract String getCode();
}
