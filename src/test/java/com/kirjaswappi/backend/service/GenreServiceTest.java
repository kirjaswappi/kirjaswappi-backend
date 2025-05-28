/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.kirjaswappi.backend.jpa.daos.GenreDao;
import com.kirjaswappi.backend.jpa.repositories.GenreRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.exceptions.GenreAlreadyExistsException;
import com.kirjaswappi.backend.service.exceptions.GenreNotFoundException;

class GenreServiceTest {
  @Mock
  private GenreRepository genreRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private GenreService genreService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Throws when genre already exists")
  void addGenreThrowsWhenExists() {
    when(genreRepository.existsByName("Fantasy")).thenReturn(true);
    Genre genre = new Genre("1", "Fantasy", null);
    assertThrows(GenreAlreadyExistsException.class, () -> {
      genreService.addGenre(genre);
    });
  }

  @Test
  @DisplayName("Adds genre successfully")
  void addGenreSuccess() {
    when(genreRepository.existsByName("SciFi")).thenReturn(false);
    Genre genre = new Genre("2", "SciFi", null);
    when(genreRepository.save(any())).thenReturn(new GenreDao());
    assertNotNull(genreService.addGenre(genre));
  }

  @Test
  @DisplayName("Returns list of genres")
  void getGenresReturnsList() {
    var dao1 = new GenreDao();
    dao1.setId("1");
    dao1.setName("Fantasy");
    var dao2 = new GenreDao();
    dao2.setId("2");
    dao2.setName("SciFi");
    when(genreRepository.findAll()).thenReturn(List.of(dao1, dao2));
    List<Genre> genres = genreService.getGenres();
    assertEquals(2, genres.size());
  }

  @Test
  @DisplayName("Updates an existing genre")
  void updateGenreUpdatesGenre() {
    var dao = new GenreDao();
    dao.setId("1");
    dao.setName("Fantasy");
    when(genreRepository.findById("1")).thenReturn(Optional.of(dao));
    when(genreRepository.save(any())).thenReturn(dao);
    Genre genre = new Genre("1", "FantasyUpdated", null);
    assertNotNull(genreService.updateGenre(genre));
  }

  @Test
  @DisplayName("Throws when updating a non-existent genre")
  void updateGenreThrowsWhenNotFound() {
    when(genreRepository.findById("1")).thenReturn(Optional.empty());
    Genre genre = new Genre("1", "FantasyUpdated", null);
    assertThrows(GenreNotFoundException.class, () -> {
      genreService.updateGenre(genre);
    });
  }

  @Test
  @DisplayName("Deletes a genre by ID")
  void deleteGenreDeletesGenre() {
    var dao = new GenreDao();
    dao.setId("1");
    when(genreRepository.findById("1")).thenReturn(Optional.of(dao));
    when(genreRepository.existsById("1")).thenReturn(true);
    doNothing().when(genreRepository).deleteById("1");
    when(userRepository.findAll()).thenReturn(List.of());
    genreService.deleteGenre("1");
    verify(genreRepository, times(1)).deleteById("1");
  }

  @Test
  @DisplayName("Throws when deleting a non-existent genre")
  void deleteGenreThrowsWhenNotFound() {
    when(genreRepository.findById("1")).thenReturn(Optional.empty());
    assertThrows(GenreNotFoundException.class, () -> {
      genreService.deleteGenre("1");
    });
  }
}
