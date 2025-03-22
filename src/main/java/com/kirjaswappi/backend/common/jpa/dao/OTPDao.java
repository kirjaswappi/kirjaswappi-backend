/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.jpa.dao;

import java.util.Date;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTPDao {
  @NotNull
  private String email;
  @NotNull
  private String otp;
  @NotNull
  private Date createdAt;
}
