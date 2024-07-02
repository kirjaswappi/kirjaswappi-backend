/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kirjaswappi.backend.common.jpa.repositories.AdminUserRepository;
import com.kirjaswappi.backend.common.service.entities.AdminUser;
import com.kirjaswappi.backend.common.service.mappers.AdminUserMapper;

@Service
public class AdminUserService {
  @Autowired
  private AdminUserRepository adminUserRepository;
  @Autowired
  private AdminUserMapper mapper;

  public AdminUser getAdminUserInfo(String username) {
    return mapper.toEntity(adminUserRepository.findByUsername(username));
  }

  public boolean isValid(String username, String password) {
    return adminUserRepository.existsByUsernameAndPassword(username, password);
  }
}