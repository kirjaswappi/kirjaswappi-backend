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

import io.jsonwebtoken.MalformedJwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1. Extract Token
    String jwt = JwtUtil.extractJwtToken(request);

    // 2. Early Exit if No Token
    if (jwt == null || SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3. Validate Token and User
    validateTokenAndUser(request, jwt);

    // 4. Continue Filter Chain
    filterChain.doFilter(request, response);
  }

  private void validateTokenAndUser(HttpServletRequest request, String jwt) {
    try {
      String username = JwtUtil.extractUsername(jwt);
      AdminUser userDetails = adminUserService.getAdminUserInfo(username);
      if (JwtUtil.validateJwtToken(jwt, userDetails))
        setAuthentication(request, jwt, userDetails);
    } catch (MalformedJwtException | AuthenticationException e) {
      SecurityContextHolder.clearContext();
    }
  }

  private void setAuthentication(HttpServletRequest request, String jwt, AdminUser userDetails) {
    SecurityContextHolder.getContext().setAuthentication(
        createAuthenticationToken(jwt, userDetails, request));
  }

  private UsernamePasswordAuthenticationToken createAuthenticationToken(String jwt, AdminUser userDetails,
      HttpServletRequest request) {
    String role = JwtUtil.extractRole(jwt);
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        userDetails, null, authorities);
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return authToken;
  }

}