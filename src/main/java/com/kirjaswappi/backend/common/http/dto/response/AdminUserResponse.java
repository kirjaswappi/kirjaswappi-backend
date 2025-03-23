/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dto.response;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.common.service.entity.AdminUser;

@Getter
@Setter
public class AdminUserResponse {
  private String username;
  private String role;

  public AdminUserResponse(AdminUser adminUser) {
    this.username = adminUser.getUsername();
    this.role = adminUser.getRole();
  }
}
