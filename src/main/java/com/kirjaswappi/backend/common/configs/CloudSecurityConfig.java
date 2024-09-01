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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.util.pattern.PathPatternParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirjaswappi.backend.common.components.FilterApiRequest;
import com.kirjaswappi.backend.common.http.ErrorResponse;

@Configuration
@EnableWebSecurity
@Profile("cloud")
public class CloudSecurityConfig implements WebMvcConfigurer {
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*")); // Allow all origins or customize as per your need
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
        .csrf(csrf -> csrf.disable()) // Disable CSRF protection
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

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.setPatternParser(new PathPatternParser());
  }

  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    resolvers.add(0, (request, response, handler, ex) -> {
      if (ex instanceof NoHandlerFoundException || ex instanceof NoResourceFoundException) {
        return handlePathNotFoundException(response, request.getRequestURI());
      }
      return null;
    });
  }

  private ModelAndView handlePathNotFoundException(HttpServletResponse response, String path) {
    ErrorResponse errorResponse = new ErrorResponse(new ErrorResponse.Error("pathNotFound", "Path not found", path));
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    response.setContentType("application/json");
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ModelAndView();
  }

  static class Scopes {
    public static final String ADMIN = "Admin";
    public static final String USER = "User";
  }
}