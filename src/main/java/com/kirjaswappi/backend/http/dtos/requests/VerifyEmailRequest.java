/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.common.service.entities.OTP;
import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.exceptions.BadRequest;

@Getter
@Setter
public class VerifyEmailRequest {
  private String email;
  private String otp;

  public OTP toEntity() {
    this.validateProperties(this.email, this.otp);
    return new OTP(this.email.toLowerCase(), this.otp, new Date());
  }

  private void validateProperties(String email, String otp) {
    if (!ValidationUtil.validateEmail(email)) {
      throw new BadRequest("invalidEmailAddress", email);
    }
    if (!ValidationUtil.validateOtp(otp)) {
      throw new BadRequest("invalidOtp", otp);
    }
  }
}
