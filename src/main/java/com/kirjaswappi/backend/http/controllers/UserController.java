/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.*;

import java.io.IOException;
import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.service.OTPService;
import com.kirjaswappi.backend.http.dtos.requests.*;
import com.kirjaswappi.backend.http.dtos.responses.*;
import com.kirjaswappi.backend.service.UserService;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@RestController
@RequestMapping(API_BASE + USERS)
@Validated
public class UserController {
  @Autowired
  private UserService userService;
  @Autowired
  private OTPService otpService;

  @PostMapping(SIGNUP)
  @Operation(summary = "Create user.", responses = {
      @ApiResponse(responseCode = "201", description = "User created.") })
  public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest user) throws IOException {
    User savedUser = userService.addUser(user.toEntity());
    otpService.saveAndSendOTP(savedUser.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(savedUser));
  }

  @PostMapping(VERIFY_EMAIL)
  @Operation(summary = "Verify email.", responses = {
      @ApiResponse(responseCode = "200", description = "Email verified.") })
  public ResponseEntity<VerifyEmailResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
    String email = otpService.verifyOTPByEmail(request.toEntity());
    String verifiedEmail = userService.verifyEmail(email);
    return ResponseEntity.status(HttpStatus.OK).body(new VerifyEmailResponse(verifiedEmail));
  }

  @PostMapping(FAVOURITE_BOOKS)
  @Operation(summary = "Add a favourite book to a user.", responses = {
      @ApiResponse(responseCode = "200", description = "Book added.") })
  public ResponseEntity<UserResponse> addFavouriteBook(@Valid @RequestBody AddFavouriteBookRequest request) {
    User entity = request.toEntity();
    User updatedUser = userService.addFavouriteBook(entity);
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(updatedUser));
  }

  @PutMapping(ID)
  @Operation(summary = "Update user.", responses = {
      @ApiResponse(responseCode = "200", description = "User updated.") })
  public ResponseEntity<UpdateUserResponse> updateUser(@Parameter(description = "User ID.") @PathVariable String id,
      @Valid @RequestBody UpdateUserRequest user) {
    // validate id:
    if (!id.equals(user.getId())) {
      throw new BadRequestException("idMismatch", id, user.getId());
    }
    User updatedUser = userService.updateUser(user.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new UpdateUserResponse(updatedUser));
  }

  @GetMapping(ID)
  @Operation(summary = "Find user by User ID.", responses = {
      @ApiResponse(responseCode = "200", description = "User found.") })
  public ResponseEntity<UserResponse> getUser(@Parameter(description = "User ID.") @PathVariable String id) {
    User user = userService.getUser(id);
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
  }

  @GetMapping
  @Operation(summary = "Find all users.", responses = {
      @ApiResponse(responseCode = "200", description = "List of users.") })
  public ResponseEntity<List<UserResponse>> getUsers() {
    var userResponses = userService.getUsers().stream().map(UserResponse::new).toList();
    return ResponseEntity.status(HttpStatus.OK).body(userResponses);
  }

  @DeleteMapping(ID)
  @Operation(summary = "Delete user.", responses = {
      @ApiResponse(responseCode = "204", description = "User deleted.") })
  public ResponseEntity<Void> deleteUser(@Parameter(description = "User ID.") @PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(LOGIN)
  @Operation(summary = "Login user.", responses = {
      @ApiResponse(responseCode = "200", description = "User logged in.") })
  public ResponseEntity<UserResponse> login(@Valid @RequestBody AuthenticateUserRequest authenticateUserRequest) {
    User user = userService.verifyLogin(authenticateUserRequest.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
  }

  @PostMapping(CHANGE_PASSWORD + EMAIL)
  @Operation(summary = "Change password.", responses = {
      @ApiResponse(responseCode = "200", description = "Password changed.") })
  public ResponseEntity<ChangePasswordResponse> changePassword(
      @Parameter(description = "User email.") @PathVariable String email,
      @Valid @RequestBody ChangePasswordRequest request) {
    userService.verifyCurrentPassword(request.toVerifyPasswordEntity(email));
    String userEmail = userService.changePassword(request.toChangePasswordEntity(email));
    return ResponseEntity.status(HttpStatus.OK).body(new ChangePasswordResponse(userEmail));
  }

  @PostMapping(RESET_PASSWORD + EMAIL)
  @Operation(summary = "Reset password.", responses = {
      @ApiResponse(responseCode = "200", description = "Password reset successfully.") })
  public ResponseEntity<ResetPasswordResponse> resetPassword(
      @Parameter(description = "User email.") @PathVariable String email,
      @Valid @RequestBody ResetPasswordRequest request) {
    String userEmail = userService.changePassword(request.toUserEntity(email));
    return ResponseEntity.status(HttpStatus.OK).body(new ResetPasswordResponse(userEmail));
  }
}
