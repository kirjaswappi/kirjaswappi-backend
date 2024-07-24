/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.common.jpa.daos.AdminUserDao;
import com.kirjaswappi.backend.common.jpa.repositories.AdminUserRepository;
import com.kirjaswappi.backend.common.service.entities.AdminUser;
import com.kirjaswappi.backend.common.service.exceptions.InvalidCredentials;
import com.kirjaswappi.backend.common.service.mappers.AdminUserMapper;
import com.kirjaswappi.backend.common.utils.JwtUtil;
import com.kirjaswappi.backend.common.utils.Util;

@Service
@Transactional
public class AuthService {
  @Autowired
  private AdminUserRepository adminUserRepository;
  @Autowired
  private AdminUserMapper mapper;
  @Autowired
  private JwtUtil jwtTokenUtil;

  // validate user credentials, if exists return user info
  public AdminUser verifyLogin(AdminUser user) {
    // get salt from username:
    AdminUserDao dao = adminUserRepository.findByUsername(user.getUsername())
        .orElseThrow(() -> new InvalidCredentials("invalidCredentials"));

    // hash password with salt:
    String password = Util.hashPassword(user.getPassword(), dao.getSalt());

    // validate email and password and return user:
    return mapper.toEntity(adminUserRepository.findByUsernameAndPassword(dao.getUsername(), password)
        .orElseThrow(() -> new InvalidCredentials("invalidCredentials")));
  }

  public String generateToken(AdminUser adminUser) {
    return jwtTokenUtil.generateToken(adminUser);
  }
}
