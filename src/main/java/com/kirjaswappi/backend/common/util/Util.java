/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.util;

import java.util.Base64;

import lombok.NoArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class Util {
  public static String generateSalt() {
    return BCrypt.gensalt();
  }

  public static String hashPassword(String password, String salt) {
    return BCrypt.hashpw(password, salt);
  }

  public static MultipartFile base64ToMultipartFile(String base64, String fileName) {
    byte[] decodedBytes = Base64.getDecoder().decode(base64);
    return new Base64MultipartFile(decodedBytes, fileName, "image/jpeg");
  }
}
