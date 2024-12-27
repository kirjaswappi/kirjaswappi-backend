/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.enums;

import java.util.List;
import java.util.stream.Stream;

import com.kirjaswappi.backend.service.exceptions.BadRequestException;

public enum Condition {
  NEW,
  LIKE_NEW,
  GOOD,
  FAIR,
  POOR,
  UNREADABLE;

  public static Condition fromString(String condition) {
    for (Condition c : Condition.values()) {
      if (c.name().equalsIgnoreCase(condition)) {
        return c;
      }
    }
    throw new BadRequestException("invalidCondition", condition);
  }

  public static List<String> getSupportedConditions() {
    return Stream.of(Condition.values()).map(Enum::name).toList();
  }
}
