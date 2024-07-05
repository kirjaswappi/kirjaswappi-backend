/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.annotations.EmailValidation;
import com.kirjaswappi.backend.service.entities.User;

@Getter
@Setter
public class UserCreateRequest {
  @NotBlank(message = "First name cannot be blank")
  private String firstName;
  @NotBlank(message = "Last name cannot be blank")
  private String lastName;
  @EmailValidation(message = "Please provide a valid email address")
  private String email;
  @NotBlank(message = "Password cannot be blank")
  private String password;
  @NotBlank(message = "Street name cannot be blank")
  private String streetName;
  @NotBlank(message = "House no. cannot be blank")
  private String houseNumber;
  private int zipCode;
  @NotBlank(message = "City cannot be blank")
  private String city;
  @NotBlank(message = "Country cannot be blank")
  private String country;
  private String phoneNumber;

  public User toEntity() {
    var entity = new User();
    entity.setFirstName(this.firstName);
    entity.setLastName(this.lastName);
    entity.setEmail(this.email);
    entity.setPassword(this.password);
    entity.setStreetName(this.streetName);
    entity.setHouseNumber(this.houseNumber);
    entity.setZipCode(this.zipCode);
    entity.setCity(this.city);
    entity.setCountry(this.country);
    entity.setPhoneNumber(this.phoneNumber);
    return entity;
  }
}
