/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.*;

import java.io.IOException;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.service.OTPService;
import com.kirjaswappi.backend.http.dtos.requests.AuthenticateUserRequest;
import com.kirjaswappi.backend.http.dtos.requests.ChangePasswordRequest;
import com.kirjaswappi.backend.http.dtos.requests.CreateUserRequest;
import com.kirjaswappi.backend.http.dtos.requests.ResetPasswordRequest;
import com.kirjaswappi.backend.http.dtos.requests.UpdateUserRequest;
import com.kirjaswappi.backend.http.dtos.requests.VerifyEmailRequest;
import com.kirjaswappi.backend.http.dtos.responses.ChangePasswordResponse;
import com.kirjaswappi.backend.http.dtos.responses.CreateUserResponse;
import com.kirjaswappi.backend.http.dtos.responses.ResetPasswordResponse;
import com.kirjaswappi.backend.http.dtos.responses.UpdateUserResponse;
import com.kirjaswappi.backend.http.dtos.responses.UserResponse;
import com.kirjaswappi.backend.http.dtos.responses.VerifyEmailResponse;
import com.kirjaswappi.backend.service.UserService;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@RestController
@RequestMapping(API_BASE + USERS)
public class UserController {
  @Autowired
  private UserService userService;
  @Autowired
  private OTPService otpService;

  @PostMapping(SIGNUP)
  @Operation(summary = "Create a user.", responses = {
      @ApiResponse(responseCode = "201", description = "User created.")})
  public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest user) throws IOException {
    User savedUser = userService.addUser(user.toEntity());
    otpService.saveAndSendOTP(savedUser.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(savedUser));
  }

  @PostMapping(VERIFY_EMAIL)
  @Operation(summary = "Verify email.", responses = {
      @ApiResponse(responseCode = "200", description = "Email verified.")})
  public ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestBody VerifyEmailRequest request) {
    String email = otpService.verifyOTPByEmail(request.toEntity());
    String verifiedEmail = userService.verifyEmail(email);
    return ResponseEntity.status(HttpStatus.OK).body(new VerifyEmailResponse(verifiedEmail));
  }

  @PutMapping(ID)
  @Operation(summary = "Update a user.", responses = {
      @ApiResponse(responseCode = "200", description = "User updated.")})
  public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest user) {
    // validate id:
    if (!id.equals(user.getId())) {
      throw new BadRequestException("idMismatch", id, user.getId());
    }
    User updatedUser = userService.updateUser(user.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new UpdateUserResponse(updatedUser));
  }

  @GetMapping(ID)
  @Operation(summary = "Get a user by User Id.", responses = {
      @ApiResponse(responseCode = "200", description = "User found.")})
  public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
    User user = userService.getUser(id);
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
  }

  @GetMapping
  @Operation(summary = "Get all users.", responses = {
      @ApiResponse(responseCode = "200", description = "List of users.")})
  public ResponseEntity<List<UserResponse>> getUsers() {
    var userResponses = userService.getUsers().stream().map(UserResponse::new).toList();
    return ResponseEntity.status(HttpStatus.OK).body(userResponses);
  }

  @DeleteMapping(ID)
  @Operation(summary = "Delete a user.", responses = {
      @ApiResponse(responseCode = "204", description = "User deleted.")})
  public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(LOGIN)
  @Operation(summary = "Login a user.", responses = {
      @ApiResponse(responseCode = "200", description = "User logged in.")})
  public ResponseEntity<UserResponse> login(@RequestBody AuthenticateUserRequest authenticateUserRequest) {
    User user = userService.verifyLogin(authenticateUserRequest.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
  }

  @PostMapping(CHANGE_PASSWORD + EMAIL)
  @Operation(summary = "Change password.", responses = {
      @ApiResponse(responseCode = "200", description = "Password changed.")})
  public ResponseEntity<ChangePasswordResponse> changePassword(@PathVariable String email,
      @RequestBody ChangePasswordRequest request) {
    userService.verifyCurrentPassword(request.toVerifyPasswordEntity(email));
    String userEmail = userService.changePassword(request.toChangePasswordEntity(email));
    return ResponseEntity.status(HttpStatus.OK).body(new ChangePasswordResponse(userEmail));
  }

  @PostMapping(RESET_PASSWORD + EMAIL)
  @Operation(summary = "Reset password.", responses = {
      @ApiResponse(responseCode = "200", description = "Password reset.")})
  public ResponseEntity<ResetPasswordResponse> resetPassword(@PathVariable String email,
      @RequestBody ResetPasswordRequest request) {
    String userEmail = userService.changePassword(request.toUserEntity(email));
    return ResponseEntity.status(HttpStatus.OK).body(new ResetPasswordResponse(userEmail));
  }
}
