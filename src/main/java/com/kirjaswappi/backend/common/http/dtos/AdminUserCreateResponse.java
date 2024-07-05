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
public class AdminUserCreateResponse {
  private String username;
  private String scopes;

  public AdminUserCreateResponse(AdminUser adminUser) {
    this.username = adminUser.getUsername();
    this.scopes = adminUser.getScopes();
  }
}
