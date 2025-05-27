/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;

class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user not found by id")
  void getUserThrowsWhenNotFound() {
    when(userRepository.findByIdAndIsEmailVerifiedTrue("id")).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userService.getUser("id"));
  }

  // Add more tests for createUser, updateUser, deleteUser, etc.
}
