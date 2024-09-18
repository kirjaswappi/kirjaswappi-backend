/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.common.service.entities.AdminUser;

@Getter
@Setter
public class AdminUserCreateRequest {
  private String username;
  private String password;
  private String role;

  public AdminUser toEntity() {
    var entity = new AdminUser();
    entity.setUsername(this.username.toLowerCase());
    entity.setPassword(this.password);
    entity.setRole(this.role);
    return entity;
  }
}
