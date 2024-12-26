/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos.requests;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.common.service.entities.AdminUser;

@Getter
@Setter
public class AdminUserCreateRequest {
  @Schema(description = "The username of the admin user.", example = "admin", requiredMode = REQUIRED)
  private String username;
  @Schema(description = "The password of the admin user.", example = "password", requiredMode = REQUIRED)
  private String password;
  @Schema(description = "The role of the admin user.", example = "Admin", requiredMode = REQUIRED)
  private String role;

  public AdminUser toEntity() {
    var entity = new AdminUser();
    entity.setUsername(this.username.toLowerCase());
    entity.setPassword(this.password);
    entity.setRole(this.role);
    return entity;
  }
}
