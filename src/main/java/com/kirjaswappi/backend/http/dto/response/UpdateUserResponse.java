/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entity.User;

@Getter
@Setter
public class UpdateUserResponse {
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String streetName;
  private String houseNumber;
  private int zipCode;
  private String city;
  private String country;
  private String phoneNumber;
  private String aboutMe;
  private List<String> favGenres;

  public UpdateUserResponse(User entity) {
    this.id = entity.getId();
    this.firstName = entity.getFirstName();
    this.lastName = entity.getLastName();
    this.email = entity.getEmail();
    this.streetName = entity.getStreetName();
    this.houseNumber = entity.getHouseNumber();
    this.zipCode = entity.getZipCode();
    this.city = entity.getCity();
    this.country = entity.getCountry();
    this.phoneNumber = entity.getPhoneNumber();
    this.aboutMe = entity.getAboutMe();
    this.favGenres = entity.getFavGenres();
  }
}
