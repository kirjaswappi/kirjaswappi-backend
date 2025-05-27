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

class OTPServiceTest {
  @InjectMocks
  private OTPService otpService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should throw exception when OTP is invalid")
  void verifyOTPThrowsOnInvalid() {
    // Add logic to simulate invalid OTP and assert exception
    // Example: assertThrows(InvalidOtpException.class, () ->
    // otpService.verifyOTPByEmail(...));
  }

  // Add more tests for saveAndSendOTP, valid OTP, etc.
}
