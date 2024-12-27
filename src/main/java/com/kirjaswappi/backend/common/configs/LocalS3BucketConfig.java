/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.configs;

import io.minio.MinioClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalS3BucketConfig {
  @Bean
  public MinioClient minioClient() {
    String s3Url = "http://localhost:9000";
    String accessKey = "test";
    String secretKey = "test";
    return MinioClient.builder()
        .endpoint(s3Url)
        .credentials(accessKey, secretKey)
        .build();
  }
}
