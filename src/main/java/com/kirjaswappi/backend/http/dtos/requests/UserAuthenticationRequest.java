/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class UserAuthenticationRequest implements Serializable {
  private String email;
  private String password;

  public User toEntity() {
    this.validateProperties();
    var entity = new User();
    entity.setEmail(this.email.toLowerCase());
    entity.setPassword(this.password);
    return entity;
  }

  private void validateProperties() {
    // validate email:
    if (!ValidationUtil.validateEmail(this.email)) {
      throw new BadRequestException("invalidEmailAddress", this.email);
    }
    // validate password:
    if (!ValidationUtil.validateNotBlank(this.password)) {
      throw new BadRequestException("passwordCannotBeBlank", this.password);
    }
  }
}