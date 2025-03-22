/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.controller;

import static com.kirjaswappi.backend.common.util.Constants.*;

import java.io.IOException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.common.http.dto.request.SendOtpRequest;
import com.kirjaswappi.backend.common.http.dto.request.VerifyOtpRequest;
import com.kirjaswappi.backend.common.http.dto.response.SendOtpResponse;
import com.kirjaswappi.backend.common.http.dto.response.VerifyOtpResponse;
import com.kirjaswappi.backend.common.service.OTPService;

@RestController
@RequestMapping(API_BASE)
public class OTPController {
  @Autowired
  private OTPService otpService;

  @PostMapping(SEND_OTP)
  @Operation(summary = "Send OTP to a user.", description = "Send OTP to a user email.", responses = {
      @ApiResponse(responseCode = "200", description = "OTP sent.") })
  public ResponseEntity<SendOtpResponse> sendOTP(@RequestBody SendOtpRequest request) throws IOException {
    String userEmail = otpService.saveAndSendOTP(request.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new SendOtpResponse(userEmail));
  }

  @PostMapping(VERIFY_OTP)
  @Operation(summary = "Verify OTP.", description = "Verify OTP of a user.", responses = {
      @ApiResponse(responseCode = "200", description = "OTP Verified.") })
  public ResponseEntity<VerifyOtpResponse> verifyOTP(@RequestBody VerifyOtpRequest request) {
    String email = otpService.verifyOTPByEmail(request.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new VerifyOtpResponse(email));
  }
}
