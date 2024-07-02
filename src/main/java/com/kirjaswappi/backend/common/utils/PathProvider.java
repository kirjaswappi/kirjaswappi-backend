/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Provides static methods about the current request
 */
public class PathProvider {

  private PathProvider() {
  }

  /**
   * returns the current path of the request
   */
  public static String getCurrentPath() {
    return ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
  }

}
