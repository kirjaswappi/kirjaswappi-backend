/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.Getter;

import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
public enum SwapConditionType {
  GIVE_AWAY("GiveAway"),
  OPEN_FOR_OFFERS("OpenForOffers"),
  BY_GENRES("ByGenres"),
  BY_BOOKS("ByBooks");

  private final String code;

  SwapConditionType(String code) {
    this.code = code;
  }

  public static SwapConditionType fromCode(String code) {
    Objects.requireNonNull(code);
    return Arrays.stream(SwapConditionType.values())
        .filter(c -> c.getCode().equalsIgnoreCase(code))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("invalidSwapConditionType", code));
  }

  public static List<String> getSupportedSwapConditionTypes() {
    return Stream.of(SwapConditionType.values()).map(SwapConditionType::getCode).toList();
  }
}
