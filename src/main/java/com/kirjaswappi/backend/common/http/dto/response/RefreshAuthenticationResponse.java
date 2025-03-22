/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dto.response;

import java.io.Serializable;

public record RefreshAuthenticationResponse(String jwtToken) implements Serializable {
}
