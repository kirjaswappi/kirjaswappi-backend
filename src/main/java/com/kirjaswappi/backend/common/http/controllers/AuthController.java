/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.AUTHENTICATE;
import static com.kirjaswappi.backend.common.utils.Constants.REFRESH;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.http.dtos.AuthenticationRequest;
import com.kirjaswappi.backend.common.http.dtos.AuthenticationResponse;
import com.kirjaswappi.backend.common.http.dtos.RefreshAuthenticationRequest;
import com.kirjaswappi.backend.common.http.dtos.RefreshAuthenticationResponse;
import com.kirjaswappi.backend.common.service.AuthService;
import com.kirjaswappi.backend.common.service.entities.AdminUser;

@RestController
@RequestMapping(API_BASE + AUTHENTICATE)
public class AuthController {
  @Autowired
  private AuthService authService;

  @PostMapping
  public ResponseEntity<AuthenticationResponse> createAuthToken(@RequestBody AuthenticationRequest request) {
    AdminUser adminUser = authService.verifyLogin(request.toEntity());
    String jwtToken = authService.generateJwtToken(adminUser);
    String refreshToken = authService.generateRefreshToken(adminUser);
    return ResponseEntity.ok(new AuthenticationResponse(jwtToken, refreshToken));
  }

  @PostMapping(REFRESH)
  public ResponseEntity<RefreshAuthenticationResponse> refreshAuthToken(
      @RequestBody RefreshAuthenticationRequest request) {
    String jwtToken = authService.verifyRefreshToken(request.getRefreshToken());
    return ResponseEntity.ok(new RefreshAuthenticationResponse(jwtToken));
  }
}