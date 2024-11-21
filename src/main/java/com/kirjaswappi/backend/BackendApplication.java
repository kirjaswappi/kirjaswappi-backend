/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BackendApplication {

  public static void main(String[] args) {
    System.out.println("Hello, KirjaSwappi!");
    System.out.println("Running on profile: " + System.getProperty("spring.profiles.active"));
    SpringApplication.run(BackendApplication.class, args);
  }
}
