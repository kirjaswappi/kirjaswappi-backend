/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validation.ValidationUtil;
import com.kirjaswappi.backend.service.entity.User;
import com.kirjaswappi.backend.service.exception.BadRequestException;

@Getter
@Setter
public class ChangePasswordRequest {
  @Schema(description = "The current password of the user.", example = "password", requiredMode = REQUIRED)
  private String currentPassword;

  @Schema(description = "The new password of the user.", example = "newPassword", requiredMode = REQUIRED)
  private String newPassword;

  @Schema(description = "The confirm password of the user.", example = "newPassword", requiredMode = REQUIRED)
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
      throw new BadRequestException("invalidEmailAddress", email);
    }
    // validate current password:
    if (!ValidationUtil.validateNotBlank(this.currentPassword)) {
      throw new BadRequestException("currentPasswordCannotBeBlank", this.currentPassword);
    }
    // validate new password:
    if (!ValidationUtil.validateNotBlank(this.newPassword)) {
      throw new BadRequestException("newPasswordCannotBeBlank", this.newPassword);
    }
    // validate confirm password:
    if (!ValidationUtil.validateNotBlank(this.confirmPassword)) {
      throw new BadRequestException("confirmPasswordCannotBeBlank", this.confirmPassword);
    }
    // validate password and confirm password:
    if (!this.newPassword.equals(this.confirmPassword)) {
      throw new BadRequestException("passwordsDoNotMatch", this.confirmPassword);
    }
  }
}
