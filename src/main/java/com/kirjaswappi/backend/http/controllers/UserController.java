/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.*;

import java.io.IOException;
import java.util.List;

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
import com.kirjaswappi.backend.http.dtos.requests.ChangePasswordRequest;
import com.kirjaswappi.backend.http.dtos.requests.ResetPasswordRequest;
import com.kirjaswappi.backend.http.dtos.requests.UserAuthenticationRequest;
import com.kirjaswappi.backend.http.dtos.requests.UserCreateRequest;
import com.kirjaswappi.backend.http.dtos.requests.UserUpdateRequest;
import com.kirjaswappi.backend.http.dtos.requests.VerifyEmailRequest;
import com.kirjaswappi.backend.http.dtos.responses.ChangePasswordResponse;
import com.kirjaswappi.backend.http.dtos.responses.ResetPasswordResponse;
import com.kirjaswappi.backend.http.dtos.responses.UserCreateResponse;
import com.kirjaswappi.backend.http.dtos.responses.UserListResponse;
import com.kirjaswappi.backend.http.dtos.responses.UserResponse;
import com.kirjaswappi.backend.http.dtos.responses.UserUpdateResponse;
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
  public ResponseEntity<UserCreateResponse> createUser(@RequestBody UserCreateRequest user) throws IOException {
    User savedUser = userService.addUser(user.toEntity());
    otpService.saveAndSendOTP(savedUser.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED).body(new UserCreateResponse(savedUser));
  }

  @PostMapping(VERIFY_EMAIL)
  public ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestBody VerifyEmailRequest request) {
    String email = otpService.verifyOTPByEmail(request.toEntity());
    String verifiedEmail = userService.verifyEmail(email);
    return ResponseEntity.status(HttpStatus.OK).body(new VerifyEmailResponse(verifiedEmail));
  }

  @PutMapping(ID)
  public ResponseEntity<UserUpdateResponse> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest user) {
    // validate id:
    if (!id.equals(user.getId())) {
      throw new BadRequestException("idMismatch", id, user.getId());
    }
    User updatedUser = userService.updateUser(user.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new UserUpdateResponse(updatedUser));
  }

  @GetMapping(ID)
  public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
    User user = userService.getUser(id);
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
  }

  @GetMapping
  public ResponseEntity<List<UserListResponse>> getUsers() {
    var userListResponses = userService.getUsers().stream().map(UserListResponse::new).toList();
    return ResponseEntity.status(HttpStatus.OK).body(userListResponses);
  }

  @DeleteMapping(ID)
  public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(LOGIN)
  public ResponseEntity<UserResponse> login(@RequestBody UserAuthenticationRequest userAuthenticationRequest) {
    User user = userService.verifyLogin(userAuthenticationRequest.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
  }

  @PostMapping(CHANGE_PASSWORD + EMAIL)
  public ResponseEntity<ChangePasswordResponse> changePassword(@PathVariable String email,
      @RequestBody ChangePasswordRequest request) {
    userService.verifyCurrentPassword(request.toVerifyPasswordEntity(email));
    String userEmail = userService.changePassword(request.toChangePasswordEntity(email));
    return ResponseEntity.status(HttpStatus.OK).body(new ChangePasswordResponse(userEmail));
  }

  @PostMapping(RESET_PASSWORD + EMAIL)
  public ResponseEntity<ResetPasswordResponse> resetPassword(@PathVariable String email,
      @RequestBody ResetPasswordRequest request) {
    String userEmail = userService.changePassword(request.toUserEntity(email));
    return ResponseEntity.status(HttpStatus.OK).body(new ResetPasswordResponse(userEmail));
  }

}
