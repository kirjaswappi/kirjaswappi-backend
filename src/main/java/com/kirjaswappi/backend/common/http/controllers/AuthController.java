/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.ADMIN_USERS;
import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.AUTHENTICATE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.http.dtos.AdminUserCreateRequest;
import com.kirjaswappi.backend.common.http.dtos.AdminUserCreateResponse;
import com.kirjaswappi.backend.common.http.dtos.AuthenticationRequest;
import com.kirjaswappi.backend.common.http.dtos.AuthenticationResponse;
import com.kirjaswappi.backend.common.service.AdminUserService;
import com.kirjaswappi.backend.common.service.entities.AdminUser;

@RestController
@RequestMapping(API_BASE + ADMIN_USERS)
@Profile("cloud")
public class AuthController {
  @Autowired
  private AdminUserService adminUserService;

  @PostMapping
  public ResponseEntity<AdminUserCreateResponse> createAdminUser(@RequestBody AdminUserCreateRequest request) {
    AdminUser savedUser = adminUserService.addUser(request.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(new AdminUserCreateResponse(savedUser));
  }

  @PostMapping(AUTHENTICATE)
  public ResponseEntity<AuthenticationResponse> createAuthToken(@RequestBody AuthenticationRequest request) {
    AdminUser adminUser = adminUserService.verifyLogin(request.toEntity());
    return ResponseEntity.ok(new AuthenticationResponse(adminUserService.generateToken(adminUser)));
  }
}