/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.common.jpa.daos.AdminUserDao;
import com.kirjaswappi.backend.common.jpa.repositories.AdminUserRepository;
import com.kirjaswappi.backend.common.service.entities.AdminUser;
import com.kirjaswappi.backend.common.service.mappers.AdminUserMapper;
import com.kirjaswappi.backend.common.utils.JwtUtil;
import com.kirjaswappi.backend.common.utils.Util;
import com.kirjaswappi.backend.service.exceptions.BadRequest;
import com.kirjaswappi.backend.service.exceptions.UserNotFound;

@Service
@Transactional
public class AdminUserService {
  @Autowired
  private AdminUserRepository adminUserRepository;
  @Autowired
  private AdminUserMapper mapper;
  @Autowired
  private JwtUtil jwtTokenUtil;

  public AdminUser getAdminUserInfo(String username) {
    return mapper.toEntity(adminUserRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFound("userNotFound", username)));
  }

  public AdminUser addUser(AdminUser adminUser) {
    // validate username exists:
    if (adminUserRepository.findByUsername(adminUser.getUsername()).isPresent()) {
      throw new BadRequest("usernameAlreadyExists", adminUser.getUsername());
    }
    // add salt to password:
    String salt = Util.generateSalt();
    adminUser.setPassword(adminUser.getPassword(), salt);
    AdminUserDao dao = mapper.toDao(adminUser, salt);
    return mapper.toEntity(adminUserRepository.save(dao));
  }

  public List<AdminUser> getAdminUsers() {
    return adminUserRepository.findAll().stream().map(mapper::toEntity).toList();
  }

  public void deleteUser(String username) {
    adminUserRepository.deleteByUsername(username);
  }
}