/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.configs;

import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile("cloud")
public class MongockConfig {
  @Bean
  public MongockInitializingBeanRunner mongockInitializingBeanRunner(MongoTemplate mongoTemplate,
      ApplicationContext applicationContext) {
    return MongockSpringboot.builder()
        .setDriver(SpringDataMongoV4Driver.withDefaultLock(mongoTemplate))
        .setSpringContext(applicationContext)
        .addMigrationScanPackage("com.kirjaswappi.backend.common.migrations")
        .buildInitializingBeanRunner();
  }
}
