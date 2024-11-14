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
import com.kirjaswappi.backend.service.exceptions.GenreAlreadyExists;
import com.kirjaswappi.backend.service.exceptions.GenreNotFound;

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

  public Genre addGenre(Genre genre) {
    // check if genre already exists:
    if (genreRepository.existsByName(genre.getName())) {
      throw new GenreAlreadyExists(genre.getName());
    }
    return mapper.toEntity(genreRepository.save(mapper.toDao(genre)));
  }

  public void deleteGenre(String id) {
    // check if genre exists:
    if (!genreRepository.existsById(id)) {
      throw new GenreNotFound(id);
    }
    genreRepository.deleteById(id);
  }

}
