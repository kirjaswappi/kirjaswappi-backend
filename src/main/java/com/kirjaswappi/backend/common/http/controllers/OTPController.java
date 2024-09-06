/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.service.OTPService;

@RestController
@RequestMapping(API_BASE)
public class OTPController {
  @Autowired
  private OTPService otpService;

  @GetMapping(SEND_OTP)
  public ResponseEntity<String> sendOTP(@RequestParam String email) throws IOException {
    otpService.saveAndSendOTP(email.toLowerCase());
    return ResponseEntity.ok("OTP sent to " + email.toLowerCase() + " successfully.");
  }

  @GetMapping(VERIFY_OTP)
  public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp) throws IOException {
    otpService.verifyOTPByEmail(email.toLowerCase(), otp);
    return ResponseEntity.ok("OTP verified for " + email.toLowerCase() + " successfully.");
  }
}
