/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordResponse {
  private String message;

  public ResetPasswordResponse(String email) {
    this.message = "Password changed for user: " + email;
  }
}
