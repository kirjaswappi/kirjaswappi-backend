/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import static com.kirjaswappi.backend.common.utils.Constants.NUMERIC;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.common.jpa.repositories.OTPRepository;
import com.kirjaswappi.backend.common.service.entities.OTP;
import com.kirjaswappi.backend.common.service.mappers.OTPMapper;
import com.kirjaswappi.backend.service.exceptions.BadRequest;
import com.kirjaswappi.backend.service.exceptions.ResourceNotFound;

@Service
@Transactional
public class OTPService {
  @Autowired
  private EmailService emailService;
  @Autowired
  private OTPRepository otpRepository;
  @Autowired
  private OTPMapper otpMapper;

  private String generateOTP() {
    SecureRandom secureRandom = new SecureRandom();
    StringBuilder otp = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      otp.append(NUMERIC.charAt(secureRandom.nextInt(NUMERIC.length())));
    }
    return otp.toString();
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
    otpRepository.deleteAllByEmail(email);
  }

  public void saveAndSendOTP(String email) throws IOException {
    // Delete all the previous OTPs:
    otpRepository.deleteAllByEmail(email);

    // Generate new OTP:
    var newOTP = new OTP(email, this.generateOTP(), new Date());

    // Save the new OTP:
    var dao = otpMapper.toDao(newOTP);
    otpRepository.save(dao);

    // Send OTP via email:
    emailService.sendOTPByEmail(newOTP.getEmail(), newOTP.getOtp());
  }
}
