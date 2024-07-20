/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.configs;

import static com.kirjaswappi.backend.common.configs.CloudSecurityConfig.Scopes.ADMIN;
import static com.kirjaswappi.backend.common.utils.Constants.ADMIN_USERS;
import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.API_DOCS;
import static com.kirjaswappi.backend.common.utils.Constants.AUTHENTICATE;
import static com.kirjaswappi.backend.common.utils.Constants.HEALTH;
import static com.kirjaswappi.backend.common.utils.Constants.SWAGGER_UI;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kirjaswappi.backend.common.components.FilterApiRequest;

@Configuration
@EnableWebSecurity
@Profile("cloud")
public class CloudSecurityConfig {
  @Autowired
  FilterApiRequest filterApiRequest;

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeRequests(authorize -> authorize
            .requestMatchers(HEALTH, API_DOCS, SWAGGER_UI, API_BASE + AUTHENTICATE).permitAll()
            .requestMatchers(POST, API_BASE + ADMIN_USERS).hasAuthority(ADMIN)
            .requestMatchers(GET, API_BASE + ADMIN_USERS).hasAuthority(ADMIN)
            .requestMatchers(DELETE, API_BASE + ADMIN_USERS).hasAuthority(ADMIN)
            .anyRequest().authenticated())
        .addFilterBefore(filterApiRequest, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  static class Scopes {
    public static final String ADMIN = "Admin";
    public static final String USER = "User";
  }
}
