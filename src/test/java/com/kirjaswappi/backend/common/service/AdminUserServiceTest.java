/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class AdminUserServiceTest {
  @InjectMocks
  private AdminUserService adminUserService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should throw exception when admin user not found")
  void getAdminUserThrowsWhenNotFound() {
    // Add logic to simulate not found and assert exception
    // Example:
    // when(adminUserRepository.findById(any())).thenReturn(Optional.empty());
    // assertThrows(AdminUserNotFoundException.class, () ->
    // adminUserService.getAdminUserById(...));
  }

  // Add more tests for addUser, deleteUser, etc.
}
