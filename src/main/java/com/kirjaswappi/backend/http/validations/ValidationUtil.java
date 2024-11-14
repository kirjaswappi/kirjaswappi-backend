/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.validations;

import java.util.regex.Pattern;

public class ValidationUtil {
  public static boolean validateNotBlank(String input) {
    return input != null && !input.trim().isEmpty();
  }

  public static boolean validateEmail(String emailAddress) {
    String regexPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    return Pattern.compile(regexPattern).matcher(emailAddress).matches();
  }

  public static boolean validateOtp(String otp) {
    return otp != null
        && !otp.trim().isEmpty()
        && otp.length() == 6
        && otp.chars().allMatch(Character::isDigit);
  }
}
