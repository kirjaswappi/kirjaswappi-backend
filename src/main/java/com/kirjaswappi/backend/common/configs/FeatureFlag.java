/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.configs;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cloud")
public class FeatureFlag {
  @Value("${unleash.apiKey}")
  private static String apiKey;

  private static final UnleashConfig config = UnleashConfig.builder()
      .appName("kirjaswappi-backend")
      .instanceId("unleash-onboarding-instance")
      .unleashAPI("https://unleash.kirjaswappi.fi/api/")
      .apiKey(apiKey)
      .build();

  private static final Unleash unleash = new DefaultUnleash(config);

  public static boolean isFeatureEnabled(String featureName) {
    return unleash.isEnabled(featureName);
  }
}
