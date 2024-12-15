/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class UpdateUserRequest {
  private String id;
  private String firstName;
  private String lastName;
  private String streetName;
  private String houseNumber;
  private Integer zipCode;
  private String city;
  private String country;
  private String phoneNumber;
  private String aboutMe;
  private List<String> favGenres;

  public User toEntity() {
    this.validateProperties();
    var entity = new User();
    entity.setId(this.id);
    entity.setFirstName(this.firstName);
    entity.setLastName(this.lastName);
    entity.setStreetName(this.streetName);
    entity.setHouseNumber(this.houseNumber);
    entity.setZipCode(this.zipCode);
    entity.setCity(this.city);
    entity.setCountry(this.country);
    entity.setPhoneNumber(this.phoneNumber);
    entity.setAboutMe(this.aboutMe);
    entity.setFavGenres(this.favGenres);
    return entity;
  }

  private void validateProperties() {
    // validate first name:
    if (!ValidationUtil.validateNotBlank(this.firstName)) {
      throw new BadRequestException("firstNameCannotBeBlank", this.firstName);
    }
    // validate last name:
    if (!ValidationUtil.validateNotBlank(this.lastName)) {
      throw new BadRequestException("lastNameCannotBeBlank", this.lastName);
    }
  }
}
