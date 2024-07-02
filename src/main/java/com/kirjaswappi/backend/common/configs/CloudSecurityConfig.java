/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.configs;

import static com.kirjaswappi.backend.common.configs.CloudSecurityConfig.Scopes.ADMIN;
import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.AUTHENTICATE;
import static com.kirjaswappi.backend.common.utils.Constants.HEALTH_ENDPOINT;
import static com.kirjaswappi.backend.common.utils.Constants.ID_PATH;
import static com.kirjaswappi.backend.common.utils.Constants.SWAGGER_DOC;
import static com.kirjaswappi.backend.common.utils.Constants.SWAGGER_UI;
import static com.kirjaswappi.backend.common.utils.Constants.USERS;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import com.kirjaswappi.backend.common.components.RequestFilter;

@Configuration
@EnableWebSecurity
@Profile("cloud")
public class CloudSecurityConfig {
  @Autowired
  private RequestFilter jwtRequestFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    // @spotless:off
    http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(GET, HEALTH_ENDPOINT).permitAll()
        .requestMatchers(GET, SWAGGER_DOC).permitAll()
        .requestMatchers(GET, SWAGGER_UI).permitAll()
        .requestMatchers(POST, AUTHENTICATE).permitAll()
        // Related to Users:
        .requestMatchers(GET, API_BASE + USERS).access(hasScope(ADMIN))
        .requestMatchers(GET, API_BASE + USERS + ID_PATH).access(hasScope(ADMIN))
        .requestMatchers(POST, API_BASE + USERS).access(hasScope(ADMIN))
        .requestMatchers(PUT, API_BASE + USERS).access(hasScope(ADMIN))
        .requestMatchers(DELETE, API_BASE + USERS).access(hasScope(ADMIN)));
    // @formatter:on
    // @spotless:on
//    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  private AuthorizationManager<RequestAuthorizationContext> hasScope(String... scopes) {
    return AuthorityAuthorizationManager.hasAnyAuthority(scopes);
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Scopes {
    public static final String ADMIN = "Admin";
  }
}