/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.jpa.daos;

import java.util.Date;

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
  private String email;
  private String otp;
  private Date createdAt;
}
