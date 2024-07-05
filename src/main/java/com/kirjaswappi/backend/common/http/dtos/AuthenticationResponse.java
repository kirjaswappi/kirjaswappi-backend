/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http.dtos;

import java.io.Serializable;

import org.springframework.context.annotation.Profile;

@Profile("cloud")
public record AuthenticationResponse(String jwt) implements Serializable {
}