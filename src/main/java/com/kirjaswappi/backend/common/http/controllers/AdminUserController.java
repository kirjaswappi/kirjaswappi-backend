/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.ADMIN_USERS;
import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.USERNAME;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.http.ErrorResponse;
import com.kirjaswappi.backend.common.http.dtos.requests.AdminUserCreateRequest;
import com.kirjaswappi.backend.common.http.dtos.responses.AdminUserResponse;
import com.kirjaswappi.backend.common.service.AdminUserService;
import com.kirjaswappi.backend.common.service.entities.AdminUser;

@RestController
@RequestMapping(API_BASE + ADMIN_USERS)
@Validated
public class AdminUserController {
  @Autowired
  private AdminUserService adminUserService;

  @PostMapping
  @Operation(summary = "Create an admin user.", responses = {
      @ApiResponse(responseCode = "201", description = "Admin user created."),
      @ApiResponse(responseCode = "400", description = "Admin user already exists.", content = @Content(schema = @Schema(oneOf = ErrorResponse.class))) })
  public ResponseEntity<AdminUserResponse> createAdminUser(@Valid @RequestBody AdminUserCreateRequest request) {
    AdminUser savedUser = adminUserService.addUser(request.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(new AdminUserResponse(savedUser));
  }

  @GetMapping
  @Operation(summary = "Get all admin users.", responses = {
      @ApiResponse(responseCode = "200", description = "List of admin users.") })
  public ResponseEntity<List<AdminUserResponse>> getAdminUsers() {
    List<AdminUserResponse> userListResponses = adminUserService.getAdminUsers()
        .stream().map(AdminUserResponse::new).toList();
    return ResponseEntity.status(HttpStatus.OK).body(userListResponses);
  }

  @DeleteMapping(USERNAME)
  @Operation(summary = "Delete an admin user.", responses = {
      @ApiResponse(responseCode = "204", description = "Admin user deleted."),
      @ApiResponse(responseCode = "404", description = "Admin user not found.", content = @Content(schema = @Schema(oneOf = ErrorResponse.class))) })
  public ResponseEntity<Void> deleteAdminUser(
      @Parameter(description = "Username of the admin user.") @PathVariable String username) {
    adminUserService.deleteUser(username);
    return ResponseEntity.noContent().build();
  }
}
