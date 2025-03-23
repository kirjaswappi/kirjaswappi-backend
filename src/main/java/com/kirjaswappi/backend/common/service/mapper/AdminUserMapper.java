/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.service.mapper;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.common.jpa.dao.AdminUserDao;
import com.kirjaswappi.backend.common.service.entity.AdminUser;

@Component
public class AdminUserMapper {
  public AdminUser toEntity(AdminUserDao dao) {
    var entity = new AdminUser();
    entity.setUsername(dao.getUsername());
    entity.setPassword(dao.getPassword());
    entity.setRole(dao.getRole());
    return entity;
  }

  public AdminUserDao toDao(AdminUser user) {
    var dao = new AdminUserDao();
    dao.setUsername(user.getUsername());
    dao.setPassword(user.getPassword());
    dao.setRole(user.getRole());
    return dao;
  }
}
