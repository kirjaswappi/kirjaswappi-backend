/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class BackendApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(BackendApplication.class);
    Environment env = app.run(args).getEnvironment();

    // Dynamically set the port
    String port = env.getProperty("PORT");
    if (port != null) {
      System.setProperty("server.port", port);
    }
  }
}
