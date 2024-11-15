/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
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
    if (!ValidationUtil.validateEmail(this.email)) {
      throw new BadRequest("invalidEmailAddress", this.email);
    }
  }
}
