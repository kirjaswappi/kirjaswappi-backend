/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.validations;

import java.util.regex.Pattern;

public class UserValidation {
  public static boolean validateEmail(String emailAddress) {
    String regexPattern = "^(?=. {1,64}@)[A-Za-z0-9_-]+(\\. [A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\. [A-Za-z0-9-]+)*(\\. [A-Za-z]{2,})$";
    return Pattern.compile(regexPattern).matcher(emailAddress).matches();
  }
}
