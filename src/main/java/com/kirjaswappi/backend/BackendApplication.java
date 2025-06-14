/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class BackendApplication {
  private static final Logger logger = LoggerFactory.getLogger(BackendApplication.class);

  public static void main(String[] args) {
    String activeProfile = System.getProperty("spring.profiles.active", "default");

    logger.info("*** KirjaSwappi Backend ***");
    logger.info("Running on {} profile", activeProfile);

    SpringApplication.run(BackendApplication.class, args);

    logger.info("Application started successfully");
  }
}
