/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class BackendApplication {

  public static void main(String[] args) {
    System.out.println("*** KirjaSwappi Backend ***");
    System.out.println("Running on " + System.getProperty("spring.profiles.active") + " profile");
    SpringApplication.run(BackendApplication.class, args);
  }
}
