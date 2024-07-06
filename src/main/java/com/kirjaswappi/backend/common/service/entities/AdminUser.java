/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.kirjaswappi.backend.common.utils.Util;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {
  String username;
  String password;
  String role;

  public void setPassword(String password, String salt) {
    this.password = Util.hashPassword(password, salt);
  }
}
