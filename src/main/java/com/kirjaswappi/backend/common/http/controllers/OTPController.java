/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.SEND_OTP;
import static com.kirjaswappi.backend.common.utils.Constants.VERIFY_OTP;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.service.EmailService;
import com.kirjaswappi.backend.common.service.OTPService;
import com.kirjaswappi.backend.common.service.entities.OTP;

@RestController
public class OTPController {
  @Autowired
  private OTPService otpService;
  @Autowired
  private EmailService emailService;

  @GetMapping(SEND_OTP)
  public ResponseEntity<String> sendOTP(@RequestParam String email) throws IOException {
    OTP otp = otpService.saveOTP(new OTP(email, otpService.generateOTP(), new Date()));
    emailService.sendOTPByEmail(email, otp.getOtp());
    return ResponseEntity.ok("OTP sent to " + email + " successfully.");
  }

  @GetMapping(VERIFY_OTP)
  public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp) throws IOException {
    otpService.verifyOTPByEmail(email, otp);
    return ResponseEntity.ok("OTP verified for " + email + " successfully.");
  }
}
