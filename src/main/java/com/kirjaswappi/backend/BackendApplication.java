/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

  public static void main(String[] args) {
    String port = java.lang.System.getenv("PORT");
    if (port != null) {
      // Dynamically set the port
      System.out.println("Running Server on Port: " + port);
      System.setProperty("server.port", port);
    }
    SpringApplication.run(BackendApplication.class, args);
  }
}
