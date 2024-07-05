/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.annotations.EmailValidation;
import com.kirjaswappi.backend.service.entities.User;

@Getter
@Setter
public class UserAuthenticationRequest implements Serializable {
  @EmailValidation(message = "Please provide a valid email address")
  private String email;
  private String password;

  public User toEntity() {
    var entity = new User();
    entity.setEmail(this.email);
    entity.setPassword(this.password);
    return entity;
  }
}