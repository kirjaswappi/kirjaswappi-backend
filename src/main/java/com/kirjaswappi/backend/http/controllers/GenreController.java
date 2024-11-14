/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.GENRES;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.http.dtos.responses.GenreListResponse;
import com.kirjaswappi.backend.service.GenreService;

@RestController
@RequestMapping(API_BASE + GENRES)
public class GenreController {
  @Autowired
  private GenreService genreService;

  @GetMapping
  public ResponseEntity<List<GenreListResponse>> getGenres() {
    var genreListResponses = genreService.getGenres().stream().map(GenreListResponse::new).toList();
    return ResponseEntity.status(HttpStatus.OK).body(genreListResponses);
  }

}
