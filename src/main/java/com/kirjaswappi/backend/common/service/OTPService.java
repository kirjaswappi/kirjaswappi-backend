/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class OTPService {
  private static final String NUMERIC = "0123456789";

  public static String generateOTP() {
    SecureRandom secureRandom = new SecureRandom();
    StringBuilder otp = new StringBuilder();

    for (int i = 0; i < 6; i++) {
      otp.append(NUMERIC.charAt(secureRandom.nextInt(NUMERIC.length())));
    }

    return otp.toString();
  }
}
