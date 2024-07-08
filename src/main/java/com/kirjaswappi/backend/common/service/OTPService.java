/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import static com.kirjaswappi.backend.common.utils.Constants.NUMERIC;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kirjaswappi.backend.common.jpa.repositories.OTPRepository;
import com.kirjaswappi.backend.common.service.entities.OTP;
import com.kirjaswappi.backend.common.service.mappers.OTPMapper;
import com.kirjaswappi.backend.service.exceptions.BadRequest;
import com.kirjaswappi.backend.service.exceptions.ResourceNotFound;

@Service
public class OTPService {
  @Autowired
  private OTPRepository otpRepository;
  @Autowired
  private OTPMapper otpMapper;

  public String generateOTP() {
    SecureRandom secureRandom = new SecureRandom();
    StringBuilder otp = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      otp.append(NUMERIC.charAt(secureRandom.nextInt(NUMERIC.length())));
    }
    return otp.toString();
  }

  public OTP saveOTP(OTP otp) {
    var dao = otpMapper.toDao(otp);
    return otpMapper.toEntity(otpRepository.save(dao));
  }

  private OTP getOTP(String email) {
    return otpMapper.toEntity(otpRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFound("otpNotFoundForEmail", email)));
  }

  public void verifyOTPByEmail(String email, String otp) {
    var otpEntity = this.getOTP(email);
    // check provided OTP with the stored OTP:
    if (!otpEntity.getOtp().equals(otp)) {
      throw new BadRequest("otpDoesNotMatch", otp);
    }
    // check createAt of OTP with current time plus 15 minutes:
    if (otpEntity.getCreatedAt().getTime() + 15 * 60 * 1000 < System.currentTimeMillis()) {
      throw new BadRequest("otpExpired", otp);
    }
    // Delete the OTP after verification:
    otpRepository.delete(otpMapper.toDao(otpEntity));
  }
}
