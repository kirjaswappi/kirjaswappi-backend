/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.EMAIL;
import static com.kirjaswappi.backend.common.utils.Constants.ID;
import static com.kirjaswappi.backend.common.utils.Constants.LOGIN;
import static com.kirjaswappi.backend.common.utils.Constants.RESET_PASSWORD;
import static com.kirjaswappi.backend.common.utils.Constants.SIGNUP;
import static com.kirjaswappi.backend.common.utils.Constants.USERS;

import java.util.List;

import jakarta.validation.Valid;

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

import com.kirjaswappi.backend.http.dtos.requests.ResetPasswordRequest;
import com.kirjaswappi.backend.http.dtos.requests.UserAuthenticationRequest;
import com.kirjaswappi.backend.http.dtos.requests.UserCreateRequest;
import com.kirjaswappi.backend.http.dtos.requests.UserUpdateRequest;
import com.kirjaswappi.backend.http.dtos.responses.UserCreateResponse;
import com.kirjaswappi.backend.http.dtos.responses.UserListResponse;
import com.kirjaswappi.backend.http.dtos.responses.UserResponse;
import com.kirjaswappi.backend.http.dtos.responses.UserUpdateResponse;
import com.kirjaswappi.backend.service.UserService;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequest;

@RestController
@Validated
@RequestMapping(API_BASE + USERS)
public class UserController {
  @Autowired
  private UserService userService;

  @PostMapping(SIGNUP)
  public ResponseEntity<UserCreateResponse> createUser(@Valid @RequestBody UserCreateRequest user) {
    User savedUser = userService.addUser(user.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(new UserCreateResponse(savedUser));
  }

  @PutMapping(ID)
  public ResponseEntity<UserUpdateResponse> updateUser(@PathVariable String id,
      @Valid @RequestBody UserUpdateRequest user) {
    // validate id:
    if (!id.equals(user.getId())) {
      throw new BadRequest("idMismatch", id, user.getId());
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
    List<UserListResponse> userListResponses = userService.getUsers().stream().map(UserListResponse::new)
        .toList();
    return ResponseEntity.status(HttpStatus.OK).body(userListResponses);
  }

  @DeleteMapping(ID)
  public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(LOGIN)
  public ResponseEntity<UserResponse> login(@Valid @RequestBody UserAuthenticationRequest userAuthenticationRequest) {
    User user = userService.verifyLogin(userAuthenticationRequest.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
  }

  @PostMapping(RESET_PASSWORD + EMAIL)
  public ResponseEntity<Void> resetPassword(@PathVariable String email,
      @Valid @RequestBody ResetPasswordRequest request) {
    userService.verifyCurrentPassword(email, request.getCurrentPassword());
    userService.resetPassword(email, request.toEntity());
    return ResponseEntity.ok().build();
  }

}
