/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.jpa.repositories.GenreRepository;
import com.kirjaswappi.backend.mapper.GenreMapper;
import com.kirjaswappi.backend.service.entities.Genre;

@Service
@Transactional
public class GenreService {
  @Autowired
  GenreRepository genreRepository;
  @Autowired
  GenreMapper mapper;

  public List<Genre> getGenres() {
    return genreRepository.findAll().stream().map(mapper::toEntity).toList();
  }
}
