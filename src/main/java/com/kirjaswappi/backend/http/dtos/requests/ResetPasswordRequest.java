/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class ResetPasswordRequest {
  private String newPassword;
  private String confirmPassword;

  public User toUserEntity(String email) {
    this.validateProperties(email);
    var entity = new User();
    entity.setEmail(email.toLowerCase());
    entity.setPassword(this.newPassword);
    return entity;
  }

  private void validateProperties(String email) {
    // validate email:
    if (!ValidationUtil.validateEmail(email)) {
      throw new BadRequestException("invalidEmailAddress", email);
    }
    // validate new password:
    if (!ValidationUtil.validateNotBlank(this.newPassword)) {
      throw new BadRequestException("newPasswordCannotBeBlank", this.newPassword);
    }
    // validate confirm password:
    if (!ValidationUtil.validateNotBlank(this.confirmPassword)) {
      throw new BadRequestException("confirmPasswordCannotBeBlank", this.confirmPassword);
    }
    // validate new password and confirm password matches:
    if (!this.newPassword.equals(this.confirmPassword)) {
      throw new BadRequestException("passwordsDoNotMatch", this.confirmPassword);
    }
  }
}
