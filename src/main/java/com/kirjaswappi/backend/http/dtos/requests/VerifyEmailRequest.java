/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.common.service.entities.OTP;
import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class VerifyEmailRequest {
  @Schema(description = "The email address of the user.", example = "abc@xyz.com", requiredMode = REQUIRED)
  private String email;

  @Schema(description = "The OTP sent to the email address.", example = "123456", requiredMode = REQUIRED)
  private String otp;

  public OTP toEntity() {
    this.validateProperties(this.email, this.otp);
    return new OTP(this.email.toLowerCase(), this.otp, new Date());
  }

  private void validateProperties(String email, String otp) {
    if (!ValidationUtil.validateEmail(email)) {
      throw new BadRequestException("invalidEmailAddress", email);
    }
    if (ValidationUtil.validateOtp(otp)) {
      throw new BadRequestException("invalidOtp", otp);
    }
  }
}
