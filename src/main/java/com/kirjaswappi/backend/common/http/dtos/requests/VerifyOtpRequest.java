/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos.requests;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.common.service.entities.OTP;
import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

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
    if (!ValidationUtil.validateEmail(this.email)) {
      throw new BadRequestException("invalidEmailAddress", email);
    }
    if (!ValidationUtil.validateOtp(this.otp)) {
      throw new BadRequestException("invalidOtp", otp);
    }
  }
}
