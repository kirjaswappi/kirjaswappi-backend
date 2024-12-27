/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kirjaswappi.backend.service.exceptions.BadRequestException;

public enum Language {
  ENGLISH,
  FINNISH,
  SWEDISH,
  GERMAN,
  FRENCH,
  SPANISH,
  ITALIAN,
  RUSSIAN,
  CHINESE,
  JAPANESE,
  KOREAN,
  HINDI,
  BENGALI,
  PUNJABI,
  URDU,
  TAMIL,
  TELUGU,
  MARATHI,
  GUJARATI,
  KANNADA,
  MALAYALAM,
  ORIYA,
  ASSAMESE,
  MAITHILI,
  SANTALI,
  KASHMIRI,
  NEPALI,
  SINDHI,
  SANSKRIT,
  ARABIC,
  PERSIAN,
  TURKISH,
  GREEK,
  HEBREW,
  LATIN,
  SWAHILI,
  ZULU,
  AFRIKAANS,
  XHOSA,
  SESOTHO,
  TSONGA,
  TSWANA,
  VENDA,
  AMHARIC,
  OROMO,
  SOMALI,
  YORUBA,
  IGBO,
  HAUSA,
  AKAN,
  KIKUYU,
  KINYARWANDA,
  LUGANDA,
  SHONA,
  CHICHEWA,
  KIRUNDI,
  LINGALA,
  MALAGASY,
  SWAZI,
  TIGRINYA,
  WOLOF,
  OTHER;

  public static Language fromString(String language) {
    for (Language l : Language.values()) {
      if (l.name().equalsIgnoreCase(language)) {
        return l;
      }
    }
    throw new BadRequestException("invalidLanguage", language);
  }

  public static List<String> getSupportedLanguages() {
    return Stream.of(Language.values())
        .map(Enum::name)
        .sorted()
        .collect(Collectors.toList());
  }
}
