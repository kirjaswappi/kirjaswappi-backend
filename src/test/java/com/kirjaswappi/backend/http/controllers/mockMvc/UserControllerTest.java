/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers.mockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirjaswappi.backend.common.service.OTPService;
import com.kirjaswappi.backend.http.controllers.UserController;
import com.kirjaswappi.backend.http.dtos.requests.*;
import com.kirjaswappi.backend.http.dtos.responses.*;
import com.kirjaswappi.backend.service.UserService;
import com.kirjaswappi.backend.service.entities.User;

@ActiveProfiles("local")
@WebMvcTest(UserController.class)
public class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;
  @MockBean
  private OTPService otpService;

  private User user;
  private UserCreateRequest userCreateRequest;
  private UserCreateResponse userCreateResponse;
  private static final String API_BASE = "/api/v1/users";

  @TestConfiguration
  static class CustomMockMvcConfiguration {
    @Profile("local")
    @Bean
    public MockMvc mockMvcLocal(WebApplicationContext webApplicationContext) {
      return MockMvcBuilders.webAppContextSetup(webApplicationContext)
          .defaultRequest(get("/").header("Host", "localhost:8080"))
          .build();
    }

    @Profile("cloud")
    @Bean
    public MockMvc mockMvcCloud(WebApplicationContext webApplicationContext) {
      return MockMvcBuilders.webAppContextSetup(webApplicationContext)
          .defaultRequest(get("/").header("Host", "localhost:10000"))
          .build();
    }
  }

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId("1");
    user.setEmail("test@example.com");

    userCreateRequest = new UserCreateRequest();
    userCreateRequest.setFirstName("Test");
    userCreateRequest.setLastName("User");
    userCreateRequest.setEmail("test@example.com");
    userCreateRequest.setPassword("password");
    userCreateRequest.setConfirmPassword("password");
    userCreateResponse = new UserCreateResponse(user);
  }

  @Test
  @DisplayName("Should create a new user")
  void shouldCreateUser() throws Exception {
    when(userService.addUser(any(User.class))).thenReturn(user);
    when(otpService.saveAndSendOTP(any(String.class))).thenReturn(user.getEmail());

    mockMvc.perform(post(API_BASE + "/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userCreateRequest))
                    .header("Authorization ", "Bearer a.b.c"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(user.getEmail()));
  }

  @Test
  @DisplayName("Should verify email")
  void shouldVerifyEmail() throws Exception {
    VerifyEmailRequest request = new VerifyEmailRequest();
    request.setEmail("test@example.com");
    request.setOtp("123456");

    when(otpService.verifyOTPByEmail(any())).thenReturn("test@example.com");
    when(userService.verifyEmail(any())).thenReturn("test@example.com");

    mockMvc.perform(post(API_BASE + "/verify-email")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .header("Authorization ", "Bearer a.b.c"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("test@example.com verified successfully."));
  }

  @Test
  @DisplayName("Should update user")
  void shouldUpdateUser() throws Exception {
    UserUpdateRequest request = getUserUpdateRequest();
    User updatedUser = getUpdatedUser();

    when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

    mockMvc.perform(put(API_BASE + "/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .header("Authorization ", "Bearer a.b.c"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("UpdatedFirstName"));
  }

  @Test
  @DisplayName("Should get user by ID")
  void shouldGetUser() throws Exception {
    when(userService.getUser("1")).thenReturn(user);

    mockMvc.perform(get(API_BASE + "/1")
                    .header("Authorization ", "Bearer a.b.c"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(user.getEmail()));
  }

  @Test
  @DisplayName("Should get all users")
  void shouldGetUsers() throws Exception {
    when(userService.getUsers()).thenReturn(List.of(user));

    mockMvc.perform(get(API_BASE)
                    .header("Authorization ", "Bearer a.b.c"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value(user.getEmail()));
  }

  @Test
  @DisplayName("Should delete user by ID")
  void shouldDeleteUser() throws Exception {
    mockMvc.perform(delete(API_BASE + "/1")
        .header("Authorization ", "Bearer a.b.c"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should login user")
  void shouldLogin() throws Exception {
    UserAuthenticationRequest request = new UserAuthenticationRequest();
    request.setEmail("test@example.com");
    request.setPassword("password");

    when(userService.verifyLogin(any(User.class))).thenReturn(user);

    mockMvc.perform(post(API_BASE + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .header("Authorization ", "Bearer a.b.c"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(user.getEmail()));
  }

  @Test
  @DisplayName("Should change user password")
  void shouldChangePassword() throws Exception {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("currentPassword");
    request.setNewPassword("newPassword");
    request.setConfirmPassword("newPassword");

    when(userService.changePassword(any())).thenReturn("test@example.com");

    mockMvc.perform(post(API_BASE + "/change-password/test@example.com")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .header("Authorization ", "Bearer a.b.c"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password changed for user: test@example.com"));
  }

  @Test
  @DisplayName("Should reset user password")
  void shouldResetPassword() throws Exception {
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setNewPassword("newPassword");
    request.setConfirmPassword("newPassword");

    when(userService.changePassword(any())).thenReturn("test@example.com");

    mockMvc.perform(post(API_BASE + "/reset-password/test@example.com")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .header("Authorization ", "Bearer a.b.c"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password changed for user: test@example.com"));
  }

  private static User getUpdatedUser() {
    User updatedUser = new User();
    updatedUser.setId("1");
    updatedUser.setFirstName("UpdatedFirstName");
    updatedUser.setLastName("UpdatedLastName");
    updatedUser.setStreetName("UpdatedStreetName");
    updatedUser.setHouseNumber("UpdatedHouseNumber");
    updatedUser.setZipCode(12345);
    updatedUser.setCity("UpdatedCity");
    updatedUser.setCountry("UpdatedCountry");
    updatedUser.setPhoneNumber("UpdatedPhoneNumber");
    updatedUser.setAboutMe("UpdatedAboutMe");
    updatedUser.setFavGenres(List.of("UpdatedGenre1", "UpdatedGenre2"));
    return updatedUser;
  }

  private static UserUpdateRequest getUserUpdateRequest() {
    UserUpdateRequest request = new UserUpdateRequest();
    request.setId("1");
    request.setFirstName("UpdatedFirstName");
    request.setLastName("UpdatedLastName");
    request.setStreetName("UpdatedStreetName");
    request.setHouseNumber("UpdatedHouseNumber");
    request.setZipCode(12345);
    request.setCity("UpdatedCity");
    request.setCountry("UpdatedCountry");
    request.setPhoneNumber("UpdatedPhoneNumber");
    request.setAboutMe("UpdatedAboutMe");
    request.setFavGenres(List.of("UpdatedGenre1", "UpdatedGenre2"));
    return request;
  }
}