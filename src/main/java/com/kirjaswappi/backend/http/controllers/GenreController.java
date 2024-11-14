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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.http.dtos.requests.GenreCreateRequest;
import com.kirjaswappi.backend.http.dtos.responses.GenreResponse;
import com.kirjaswappi.backend.service.GenreService;
import com.kirjaswappi.backend.service.entities.Genre;

@RestController
@RequestMapping(API_BASE + GENRES)
public class GenreController {
  @Autowired
  private GenreService genreService;

  @GetMapping
  public ResponseEntity<List<GenreResponse>> getGenres() {
    var genreListResponses = genreService.getGenres().stream().map(GenreResponse::new).toList();
    return ResponseEntity.status(HttpStatus.OK).body(genreListResponses);
  }

  @PostMapping
  public ResponseEntity<GenreResponse> createGenre(@RequestBody GenreCreateRequest request) {
    Genre savedGenre = genreService.addGenre(request.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED).body(new GenreResponse(savedGenre));
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteGenre(@PathVariable String id) {
    genreService.deleteGenre(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
