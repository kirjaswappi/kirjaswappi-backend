/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequest;

@Getter
@Setter
public class ChangePasswordRequest {
  private String currentPassword;
  private String newPassword;
  private String confirmPassword;

  public User toChangePasswordEntity(String email) {
    this.validateProperties(email);
    var entity = new User();
    entity.setEmail(email.toLowerCase());
    entity.setPassword(this.newPassword);
    return entity;
  }

  public User toVerifyPasswordEntity(String email) {
    this.validateProperties(email);
    var entity = new User();
    entity.setEmail(email.toLowerCase());
    entity.setPassword(this.currentPassword);
    return entity;
  }

  private void validateProperties(String email) {
    // validate email:
    if (!ValidationUtil.validateEmail(email)) {
      throw new BadRequest("invalidEmailAddress", email);
    }
    // validate current password:
    if (!ValidationUtil.validateNotBlank(this.currentPassword)) {
      throw new BadRequest("currentPasswordCannotBeBlank", this.currentPassword);
    }
    // validate new password:
    if (!ValidationUtil.validateNotBlank(this.newPassword)) {
      throw new BadRequest("newPasswordCannotBeBlank", this.newPassword);
    }
    // validate confirm password:
    if (!ValidationUtil.validateNotBlank(this.confirmPassword)) {
      throw new BadRequest("confirmPasswordCannotBeBlank", this.confirmPassword);
    }
    // validate password and confirm password:
    if (!this.newPassword.equals(this.confirmPassword)) {
      throw new BadRequest("passwordsDoNotMatch", this.confirmPassword);
    }
  }
}
