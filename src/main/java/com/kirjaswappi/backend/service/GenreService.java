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
import com.kirjaswappi.backend.service.exceptions.GenreAlreadyExistsException;
import com.kirjaswappi.backend.service.exceptions.GenreNotFoundException;

@Service
@Transactional
public class GenreService {
  @Autowired
  GenreRepository genreRepository;

  public List<Genre> getGenres() {
    return genreRepository.findAll().stream().map(GenreMapper::toEntity).toList();
  }

  public Genre addGenre(Genre genre) {
    // check if genre already exists:
    if (genreRepository.existsByName(genre.getName())) {
      throw new GenreAlreadyExistsException(genre.getName());
    }
    return GenreMapper.toEntity(genreRepository.save(GenreMapper.toDao(genre)));
  }

  public void deleteGenre(String id) {
    // check if genre exists:
    if (!genreRepository.existsById(id)) {
      throw new GenreNotFoundException(id);
    }
    genreRepository.deleteById(id);
  }

  public Genre updateGenre(Genre entity) {
    var dao = genreRepository.findById(entity.getId())
        .orElseThrow(() -> new GenreNotFoundException(entity.getId()));
    dao.setName(entity.getName());
    return GenreMapper.toEntity(genreRepository.save(dao));
  }
}
