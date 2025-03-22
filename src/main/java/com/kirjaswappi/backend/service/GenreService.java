/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.jpa.dao.UserDao;
import com.kirjaswappi.backend.jpa.repository.GenreRepository;
import com.kirjaswappi.backend.jpa.repository.UserRepository;
import com.kirjaswappi.backend.mapper.GenreMapper;
import com.kirjaswappi.backend.service.entity.Genre;
import com.kirjaswappi.backend.service.exception.GenreAlreadyExistsException;
import com.kirjaswappi.backend.service.exception.GenreCannotBeDeletedException;
import com.kirjaswappi.backend.service.exception.GenreNotFoundException;

@Service
@Transactional
public class GenreService {
  @Autowired
  GenreRepository genreRepository;

  @Autowired
  UserRepository userRepository;

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
    // Check if genre exists:
    if (!genreRepository.existsById(id)) {
      throw new GenreNotFoundException(id);
    }

    // Check if genre is associated with any user or book:
    if (isIsBeingGenreUsed(id)) {
      throw new GenreCannotBeDeletedException(id);
    }

    genreRepository.deleteById(id);
  }

  private boolean isIsBeingGenreUsed(String id) {
    return userRepository.findAll().stream().anyMatch(user -> isGenreInFavGenres(user, id) || isGenreInBooks(user, id));
  }

  private boolean isGenreInFavGenres(UserDao user, String id) {
    return user.getFavGenres() != null
        && user.getFavGenres().stream().anyMatch(favGenre -> favGenre.getId().equals(id));
  }

  private boolean isGenreInBooks(UserDao user, String id) {
    return user.getBooks() != null && user.getBooks().stream()
        .anyMatch(book -> book.getGenres().stream().anyMatch(genre -> genre.getId().equals(id)) &&
            book.getExchangeCondition() != null &&
            book.getExchangeCondition().getExchangeableGenres().stream().anyMatch(g -> g.getId().equals(id)));
  }

  public Genre updateGenre(Genre entity) {
    var dao = genreRepository.findById(entity.getId())
        .orElseThrow(() -> new GenreNotFoundException(entity.getId()));
    dao.setName(entity.getName());
    return GenreMapper.toEntity(genreRepository.save(dao));
  }
}
