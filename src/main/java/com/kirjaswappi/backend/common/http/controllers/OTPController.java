/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.SEND_OTP;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.service.EmailService;
import com.kirjaswappi.backend.common.service.OTPService;

@RestController
public class OTPController {
  @Autowired
  private EmailService emailService;

  @GetMapping(SEND_OTP)
  public ResponseEntity<String> sendOTP(@RequestParam String userEmail) throws IOException {
    emailService.sendOTPByEmail(userEmail, OTPService.generateOTP());
    return ResponseEntity.ok("OTP sent to " + userEmail + " successfully.");
  }
}
