/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.components;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kirjaswappi.backend.common.service.AdminUserService;
import com.kirjaswappi.backend.common.service.entities.AdminUser;
import com.kirjaswappi.backend.common.utils.JwtUtil;

@Component
@Profile("cloud")
public class FilterApiRequest extends OncePerRequestFilter {
  @Autowired
  private AdminUserService adminUserService;
  @Autowired
  private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1. Extract Token
    String jwt = jwtUtil.extractJwtToken(request);

    // 2. Early Exit if No Token
    if (jwt == null || SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3. Validate Token and User
    String username = jwtUtil.extractUsername(jwt);
    AdminUser userDetails = adminUserService.getAdminUserInfo(username);

    if (jwtUtil.validateJwtToken(jwt, userDetails)) {
      // 4. Set Authentication (encapsulate token creation)
      SecurityContextHolder.getContext().setAuthentication(
          createAuthenticationToken(jwt, userDetails, request));
    }

    filterChain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken createAuthenticationToken(String jwt, AdminUser userDetails,
      HttpServletRequest request) {
    String role = jwtUtil.extractRole(jwt);
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        userDetails, null, authorities);
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return authToken;
  }

}