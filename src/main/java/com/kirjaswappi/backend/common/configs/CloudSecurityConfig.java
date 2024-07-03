/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.configs;

import static com.kirjaswappi.backend.common.utils.Constants.ACTUATOR;
import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.API_DOCS;
import static com.kirjaswappi.backend.common.utils.Constants.AUTHENTICATE;
import static com.kirjaswappi.backend.common.utils.Constants.SWAGGER_UI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeRequests()
        .requestMatchers(ACTUATOR, API_DOCS, SWAGGER_UI, API_BASE + AUTHENTICATE)
        .permitAll()
        .anyRequest()
        .authenticated();
    http.addFilterBefore(filterApiRequest, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
