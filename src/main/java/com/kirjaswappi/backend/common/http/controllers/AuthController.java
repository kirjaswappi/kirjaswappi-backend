/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.AUTHENTICATE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.http.dtos.AuthenticationRequest;
import com.kirjaswappi.backend.common.http.dtos.AuthenticationResponse;
import com.kirjaswappi.backend.common.service.AdminUserService;
import com.kirjaswappi.backend.common.service.entities.AdminUser;
import com.kirjaswappi.backend.common.utils.JwtUtil;
import com.kirjaswappi.backend.service.exceptions.BadRequest;

@RestController
@RequestMapping(API_BASE + AUTHENTICATE)
public class AuthController {
  @Autowired
  private JwtUtil jwtTokenUtil;
  @Autowired
  private AdminUserService adminUserService;

  @PostMapping
  public ResponseEntity<AuthenticationResponse> createAuthToken(@RequestBody AuthenticationRequest request) {
    if (adminUserService.isValid(request.getUsername(), request.getPassword())) {
      AdminUser adminUser = adminUserService.getAdminUserInfo(request.getUsername());
      final String jwt = jwtTokenUtil.generateToken(adminUser);
      return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
    throw new BadRequest("invalidCredentials");
  }
}