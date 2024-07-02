/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import org.bson.types.Binary;

import com.kirjaswappi.backend.service.entities.User;

@Getter
@Setter
public class UserCreateRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String streetName;
  private String houseNumber;
  private String zipCode;
  private String city;
  private String country;
  private String phoneNumber;
  private Binary profilePicture;

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
    entity.setProfilePicture(this.profilePicture);
    return entity;
  }
}
