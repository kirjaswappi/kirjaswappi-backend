/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpResponse {
  private String message;

  public VerifyOtpResponse(String email) {
    this.message = "OTP verified for " + email + " successfully.";
  }
}
