/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos.requests;

import java.util.Date;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.common.service.entities.OTP;
import com.kirjaswappi.backend.service.exceptions.BadRequest;

@Getter
@Setter
public class VerifyOtpRequest {
  private String email;
  private String otp;

  public OTP toEntity() {
    this.validateProperties();
    return new OTP(this.email.toLowerCase(), this.otp, new Date());
  }

  private void validateProperties() {
    if (!validateEmail(this.email)) {
      throw new BadRequest("invalidEmailAddress", email);
    }
    if (!validateOtp(this.otp)) {
      throw new BadRequest("invalidOtp", otp);
    }
  }

  private static boolean validateOtp(String otp) {
    return otp != null
        && !otp.trim().isEmpty()
        && otp.length() == 6
        && otp.chars().allMatch(Character::isDigit);
  }

  private static boolean validateEmail(String emailAddress) {
    String regexPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    return Pattern.compile(regexPattern).matcher(emailAddress).matches();
  }
}
