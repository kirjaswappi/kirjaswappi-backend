/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dto.response;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entity.User;

@Getter
@Setter
public class CreateUserResponse {
  private String id;
  private String firstName;
  private String lastName;
  private String email;

  public CreateUserResponse(User entity) {
    this.id = entity.getId();
    this.firstName = entity.getFirstName();
    this.lastName = entity.getLastName();
    this.email = entity.getEmail();
  }
}
