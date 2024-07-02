/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kirjaswappi.backend.common.jpa.daos.AdminUserDao;
import com.kirjaswappi.backend.common.jpa.repositories.AdminUserRepository;

@Service
public class AdminUserService {
  @Autowired
  private AdminUserRepository adminUserRepository;

  public AdminUserDao getAdminUserInfo(String username) {
    return adminUserRepository.findByUsername(username);
  }

  public boolean isValid(String username, String password) {
    return adminUserRepository.existsByUsernameAndPassword(username, password);
  }
}
