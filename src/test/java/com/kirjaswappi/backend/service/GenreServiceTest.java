/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.kirjaswappi.backend.jpa.repositories.GenreRepository;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.exceptions.GenreAlreadyExistsException;

class GenreServiceTest {
  @Mock
  private GenreRepository genreRepository;
  @InjectMocks
  private GenreService genreService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should throw GenreAlreadyExistsException when genre already exists")
  void addGenreThrowsWhenExists() {
    when(genreRepository.existsByName("Fantasy")).thenReturn(true);
    assertThrows(GenreAlreadyExistsException.class, () -> genreService.addGenre(new Genre("1", "Fantasy", null)));
  }

  // Add more tests for getGenres, updateGenre, deleteGenre, etc.
}
