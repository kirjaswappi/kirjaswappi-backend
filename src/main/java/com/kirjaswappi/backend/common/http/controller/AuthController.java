/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controller;

import static com.kirjaswappi.backend.common.util.Constants.API_BASE;
import static com.kirjaswappi.backend.common.util.Constants.AUTHENTICATE;
import static com.kirjaswappi.backend.common.util.Constants.REFRESH;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.http.dto.request.AuthenticationRequest;
import com.kirjaswappi.backend.common.http.dto.request.RefreshAuthenticationRequest;
import com.kirjaswappi.backend.common.http.dto.response.AuthenticationResponse;
import com.kirjaswappi.backend.common.http.dto.response.RefreshAuthenticationResponse;
import com.kirjaswappi.backend.common.service.AuthService;
import com.kirjaswappi.backend.common.service.entity.AdminUser;

@RestController
@RequestMapping(API_BASE + AUTHENTICATE)
@Validated
public class AuthController {
  @Autowired
  private AuthService authService;

  @PostMapping
  @Operation(summary = "Authenticate an admin user.", description = "Authenticate via admin user and generate a JWT token.", responses = {
      @ApiResponse(responseCode = "200", description = "JWT token generated."),
      @ApiResponse(responseCode = "401", description = "Invalid credentials."),
  })
  public ResponseEntity<AuthenticationResponse> createAuthToken(@Valid @RequestBody AuthenticationRequest request) {
    AdminUser adminUser = authService.verifyLogin(request.toEntity());
    String jwtToken = authService.generateJwtToken(adminUser);
    String refreshToken = authService.generateRefreshToken(adminUser);
    return ResponseEntity.ok(new AuthenticationResponse(jwtToken, refreshToken));
  }

  @PostMapping(REFRESH)
  @Operation(summary = "Refresh an authentication token.", description = "Refresh an authentication token using a refresh token.", responses = {
      @ApiResponse(responseCode = "200", description = "JWT token refreshed.") })
  public ResponseEntity<RefreshAuthenticationResponse> refreshAuthToken(
      @Valid @RequestBody RefreshAuthenticationRequest request) {
    String jwtToken = authService.verifyRefreshToken(request.getRefreshToken());
    return ResponseEntity.ok(new RefreshAuthenticationResponse(jwtToken));
  }
}