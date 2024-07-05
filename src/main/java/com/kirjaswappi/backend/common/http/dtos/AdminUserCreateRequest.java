/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.annotation.Profile;

import com.kirjaswappi.backend.common.service.entities.AdminUser;

@Getter
@Setter
@Profile("cloud")
public class AdminUserCreateRequest {
  private String username;
  private String password;
  private String scopes;

  public AdminUser toEntity() {
    var entity = new AdminUser();
    entity.setUsername(this.username);
    entity.setPassword(this.password);
    entity.setScopes(this.scopes);
    return entity;
  }
}
