/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshAuthenticationRequest {
  private String refreshToken;
}
