/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos.requests;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.exceptions.BadRequest;

@Getter
@Setter
public class SendOtpRequest {
  private String email;

  public String toEntity() {
    this.validateProperties();
    return this.email.toLowerCase();
  }

  private void validateProperties() {
    if (!validateEmail(this.email)) {
      throw new BadRequest("invalidEmailAddress", this.email);
    }
  }

  private static boolean validateEmail(String emailAddress) {
    String regexPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    return Pattern.compile(regexPattern).matcher(emailAddress).matches();
  }
}
