/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequest;

@Getter
@Setter
public class ResetPasswordRequest {
  private String currentPassword;
  private String newPassword;
  private String confirmPassword;

  public User toEntity() {
    if (!this.newPassword.equals(this.confirmPassword)) {
      throw new BadRequest("passwordMismatch");
    }
    var entity = new User();
    entity.setPassword(this.newPassword);
    return entity;
  }
}
