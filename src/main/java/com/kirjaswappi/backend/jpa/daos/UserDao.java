/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.daos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDao {
  @Id
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String salt;
  private String streetName;
  private String houseNumber;
  private int zipCode;
  private String city;
  private String country;
  private String phoneNumber;
}
