/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.UserValidation;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequest;

@Getter
@Setter
public class ResetPasswordRequest {
  private String otp;
  private String newPassword;
  private String confirmPassword;

  public User toEntity(String email) {
    this.validateProperties(email);
    var entity = new User();
    entity.setPassword(this.newPassword);
    return entity;
  }

  private void validateProperties(String email) {
    // validate email:
    if (!UserValidation.validateEmail(email)) {
      throw new BadRequest("invalidEmailAddress", email);
    }
    // validate current password:
    if (!UserValidation.validateNotBlank(this.otp)) {
      throw new BadRequest("otpCannotBeBlank", this.otp);
    }
    // validate new password:
    if (!UserValidation.validateNotBlank(this.newPassword)) {
      throw new BadRequest("newPasswordCannotBeBlank", this.newPassword);
    }
    // validate confirm password:
    if (!UserValidation.validateNotBlank(this.confirmPassword)) {
      throw new BadRequest("confirmPasswordCannotBeBlank", this.confirmPassword);
    }
    // validate password and confirm password:
    if (!this.newPassword.equals(this.confirmPassword)) {
      throw new BadRequest("passwordsDoNotMatch", this.confirmPassword);
    }
  }
}
