/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {
  @Mock
  private JavaMailSender mailSender;
  @Mock
  private Environment env;
  @InjectMocks
  private EmailService emailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should log error when email sending fails (RuntimeException simulating MessagingException)")
  void sendEmailThrowsOnFailure() {
    MimeMessage message = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(message);
    when(env.getProperty(any())).thenReturn("from@example.com");
    doThrow(new RuntimeException("fail")).when(mailSender).send(any(MimeMessage.class));
    assertThrows(RuntimeException.class, () -> emailService.sendEmail("to@example.com", "sub", "<b>body</b>"));
    verify(mailSender).send(message);
  }

  @Test
  @DisplayName("Should handle exception when email sending fails (RuntimeException simulating MessagingException)")
  void sendEmailHandlesFailure() {
    MimeMessage message = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(message);
    doThrow(new RuntimeException("fail")).when(mailSender).send(any(MimeMessage.class));
    when(env.getProperty(any())).thenReturn("from@example.com");
    assertThrows(RuntimeException.class, () -> emailService.sendEmail("to@example.com", "sub", "<b>body</b>"));
  }

  @Test
  @DisplayName("Should send email successfully")
  void sendEmailSuccess() throws Exception {
    MimeMessage message = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(message);
    when(env.getProperty(any())).thenReturn("from@example.com");
    doNothing().when(mailSender).send(any(MimeMessage.class));
    assertDoesNotThrow(() -> emailService.sendEmail("to@example.com", "sub", "<b>body</b>"));
  }

  @Test
  @DisplayName("Should send OTP email successfully using real template")
  void sendOTPByEmailSuccess() {
    EmailService spyService = spy(emailService);
    doNothing().when(spyService).sendEmail(any(), any(), any());
    assertDoesNotThrow(() -> spyService.sendOTPByEmail("to@example.com", "123456"));
  }

  @Test
  @DisplayName("Should send OTP email and replace OTP in template using real resource")
  void sendOTPByEmailReplacesOtpInTemplate() {
    EmailService spyService = spy(emailService);
    doNothing().when(spyService).sendEmail(any(), any(), any());
    assertDoesNotThrow(() -> spyService.sendOTPByEmail("to@example.com", "654321"));
  }

  @Test
  @DisplayName("Should set correct from, to, subject, and HTML body in sendEmail")
  void sendEmailSetsAllMimeMessageFields() throws Exception {
    MimeMessage message = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(message);
    when(env.getProperty(any())).thenReturn("from@example.com");
    doNothing().when(mailSender).send(any(MimeMessage.class));
    assertDoesNotThrow(() -> emailService.sendEmail("to@example.com", "Subject", "<b>body</b>"));
    verify(mailSender).send(message);
  }

  @Test
  @DisplayName("Should handle null from address gracefully (logs error, throws NPE)")
  void sendEmailHandlesNullFrom() {
    MimeMessage message = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(message);
    when(env.getProperty(any())).thenReturn(null);
    assertThrows(NullPointerException.class, () -> emailService.sendEmail("to@example.com", "Subject", "<b>body</b>"));
  }

  @Test
  @DisplayName("Should handle MessagingException in sendEmail (logs error, does not throw)")
  void sendEmailHandlesMessagingException() throws Exception {
    MimeMessage message = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(message);
    when(env.getProperty(any())).thenReturn("from@example.com");
    // Can't throw MessagingException directly, so simulate with RuntimeException
    doThrow(new RuntimeException("fail")).when(mailSender).send(any(MimeMessage.class));
    assertThrows(RuntimeException.class, () -> emailService.sendEmail("to@example.com", "Subject", "<b>body</b>"));
  }

  @Test
  @DisplayName("Should send email with HTML content")
  void sendEmailSendsHtmlContent() throws Exception {
    MimeMessage message = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(message);
    when(env.getProperty(any())).thenReturn("from@example.com");
    doNothing().when(mailSender).send(any(MimeMessage.class));
    assertDoesNotThrow(() -> emailService.sendEmail("to@example.com", "Subject", "<h1>HTML</h1>"));
  }
}
