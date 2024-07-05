/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.utils;

import lombok.NoArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCrypt;

@NoArgsConstructor
public class Util {
  public static String generateSalt() {
    return BCrypt.gensalt();
  }

  public static String hashPassword(String password, String salt) {
    return BCrypt.hashpw(password, salt);
  }
}
