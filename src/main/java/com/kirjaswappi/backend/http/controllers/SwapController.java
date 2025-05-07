/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.kirjaswappi.backend.http.dtos.requests.CreateSwapRequest;
import com.kirjaswappi.backend.http.dtos.responses.SwapRequestResponse;
import com.kirjaswappi.backend.service.SwapService;
import com.kirjaswappi.backend.service.entities.SwapRequest;

@RestController
@RequestMapping(API_BASE + SWAP_REQUESTS)
@Validated
public class SwapController {
  @Autowired
  private SwapService swapService;

  @PostMapping
  public ResponseEntity<SwapRequestResponse> createSwapRequest(@Valid @RequestBody CreateSwapRequest request) {
    SwapRequest createdSwapRequest = swapService.createSwapRequest(request.toEntity());
    return ResponseEntity.status(HttpStatus.OK).body(new SwapRequestResponse(createdSwapRequest));
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAllSwapRequests() {
    swapService.deleteAllSwapRequests();
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}