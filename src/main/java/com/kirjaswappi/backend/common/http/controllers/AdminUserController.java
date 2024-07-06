/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.ADMIN_USERS;
import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.http.dtos.AdminUserCreateRequest;
import com.kirjaswappi.backend.common.http.dtos.AdminUserResponse;
import com.kirjaswappi.backend.common.service.AdminUserService;
import com.kirjaswappi.backend.common.service.entities.AdminUser;

@RestController
@RequestMapping(API_BASE + ADMIN_USERS)
@Profile("cloud")
public class AdminUserController {
  @Autowired
  private AdminUserService adminUserService;

  @PostMapping
  public ResponseEntity<AdminUserResponse> createAdminUser(@RequestBody AdminUserCreateRequest request) {
    AdminUser savedUser = adminUserService.addUser(request.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(new AdminUserResponse(savedUser));
  }

  @GetMapping
  public ResponseEntity<List<AdminUserResponse>> getAdminUsers() {
    List<AdminUserResponse> userListResponses = adminUserService.getAdminUsers()
        .stream().map(AdminUserResponse::new).toList();
    return ResponseEntity.status(HttpStatus.OK).body(userListResponses);
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAdminUser(@PathVariable String username) {
    adminUserService.deleteUser(username);
    return ResponseEntity.noContent().build();
  }
}
