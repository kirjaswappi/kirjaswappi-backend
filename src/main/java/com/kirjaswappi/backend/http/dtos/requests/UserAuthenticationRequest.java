/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.UserValidation;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequest;

@Getter
@Setter
public class UserAuthenticationRequest implements Serializable {
  private String email;
  private String password;

  public User toEntity() {
    validateProperties();
    var entity = new User();
    entity.setEmail(this.email);
    entity.setPassword(this.password);
    return entity;
  }

  private void validateProperties() {
    // validate email:
    if (!UserValidation.validateEmail(this.email)) {
      throw new BadRequest("invalidEmailAddress", this.email);
    }
    // validate password:
    if (!UserValidation.validateNotBlank(this.password)) {
      throw new BadRequest("passwordCannotBeBlank", this.password);
    }
  }
}