/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAllBooksFilter {
  String search;
  String language;
  String condition;
  String genre;
}
