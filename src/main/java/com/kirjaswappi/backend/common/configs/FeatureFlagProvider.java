/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.configs;

import jakarta.annotation.PostConstruct;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cloud")
public class FeatureFlagProvider {
  @Value("${unleash.instanceId}")
  private String instanceId;

  @Value("${unleash.url}")
  private String apiUrl;

  @Value("${unleash.apiKey}")
  private String apiKey;

  private Unleash unleash;

  @PostConstruct
  private void initializeUnleash() {
    UnleashConfig config = UnleashConfig.builder()
        .appName("Kirjaswappi-backend")
        .instanceId(instanceId)
        .unleashAPI(apiUrl)
        .apiKey(apiKey)
        .build();
    this.unleash = new DefaultUnleash(config);
  }

  public boolean isFeatureEnabled(String featureName) {
    return unleash.isEnabled(featureName);
  }
}
