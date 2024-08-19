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

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kirjaswappi.backend.common.components.FilterApiRequest;

@Configuration
@EnableWebSecurity
@Profile("cloud")
public class CloudSecurityConfig {
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*")); // Allow all origins or customize as per your need
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // Allow all
                                                                                                        // HTTP methods
                                                                                                        // or customize
                                                                                                        // as per your
                                                                                                        // need
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(List.of("*")); // Allow all headers or customize as per your need

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(FilterApiRequest filterApiRequest, HttpSecurity http)
      throws Exception {
    return http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(crsf -> crsf.disable())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HEALTH, API_DOCS, SWAGGER_UI, API_BASE + AUTHENTICATE).permitAll()
            .requestMatchers(POST, API_BASE + ADMIN_USERS).hasAuthority(ADMIN)
            .requestMatchers(GET, API_BASE + ADMIN_USERS).hasAuthority(ADMIN)
            .requestMatchers(DELETE, API_BASE + ADMIN_USERS).hasAuthority(ADMIN)
            .anyRequest().authenticated())
        .addFilterBefore(filterApiRequest, UsernamePasswordAuthenticationFilter.class)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .build();
  }

  static class Scopes {
    public static final String ADMIN = "Admin";
    public static final String USER = "User";
  }
}