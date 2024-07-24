/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.common.service.entities.AdminUser;

@Getter
@Setter
public class AuthenticationRequest {
  private String username;
  private String password;

  public AdminUser toEntity() {
    var entity = new AdminUser();
    entity.setUsername(this.username);
    entity.setPassword(this.password);
    return entity;
  }
}