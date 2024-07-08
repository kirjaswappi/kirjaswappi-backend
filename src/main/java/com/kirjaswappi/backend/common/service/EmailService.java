/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import java.io.IOException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Async
public class EmailService {
  @Autowired
  private JavaMailSender mailSender;
  @Autowired
  private Environment env;

  private Logger logger = LoggerFactory.getLogger(EmailService.class);

  public void sendOTPByEmail(String email, String otp) throws IOException {
    String subject = "OTP Verification";
    Resource resource = new ClassPathResource("templates/EmailTemplate.html");
    String template = new String(resource.getInputStream().readAllBytes());
    String emailText = template.replace("{{otp}}", otp);
    sendEmail(email, subject, emailText);
  }

  public void sendEmail(String to, String subject, String htmlBody) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
    try {
      helper.setFrom(env.getProperty("spring.mail.username"));
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlBody, true); // Set the second argument to true for HTML content
      mailSender.send(message);
    } catch (MessagingException e) {
      logger.error("Failed to send email to " + to, e);
    }
  }
}
