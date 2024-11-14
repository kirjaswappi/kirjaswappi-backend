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
public class UserCreateRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String confirmPassword;

  public User toEntity() {
    this.validateProperties();
    var entity = new User();
    entity.setFirstName(this.firstName);
    entity.setLastName(this.lastName);
    entity.setEmail(this.email.toLowerCase());
    entity.setPassword(this.password);
    return entity;
  }

  private void validateProperties() {
    // validate first name:
    if (!ValidationUtil.validateNotBlank(this.firstName)) {
      throw new BadRequest("firstNameCannotBeBlank", this.firstName);
    }
    // validate last name:
    if (!ValidationUtil.validateNotBlank(this.lastName)) {
      throw new BadRequest("lastNameCannotBeBlank", this.lastName);
    }
    // validate email:
    if (!ValidationUtil.validateEmail(this.email)) {
      throw new BadRequest("invalidEmailAddress", this.email);
    }
    // validate password:
    if (!ValidationUtil.validateNotBlank(this.password)) {
      throw new BadRequest("passwordCannotBeBlank", this.password);
    }
    // validate confirm password:
    if (!ValidationUtil.validateNotBlank(this.confirmPassword)) {
      throw new BadRequest("confirmPasswordCannotBeBlank", this.confirmPassword);
    }
    // validate password and confirm password:
    if (!this.password.equals(this.confirmPassword)) {
      throw new BadRequest("passwordsDoNotMatch", this.confirmPassword);
    }
  }
}
