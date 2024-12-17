/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.Photo;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class CreateBookRequest {
  private String title;
  private String author;
  private String description;
  private String language;
  private String condition;
  private List<String> genres;
  private MultipartFile coverPhoto;
  private String ownerId;

  public Book toEntity() {
    this.validateProperties();
    var book = new Book();
    book.setTitle(title);
    book.setAuthor(author);
    book.setDescription(description);
    book.setLanguage(language);
    book.setCondition(condition);
    book.setGenres(genres);
    if (coverPhoto != null) {
      var photo = new Photo();
      photo.setFile(coverPhoto);
      book.setCoverPhoto(photo);
    }
    var user = new User();
    user.setId(ownerId);
    book.setOwner(user);
    return book;
  }

  private void validateProperties() {
    if (!ValidationUtil.validateNotBlank(this.title)) {
      throw new BadRequestException("bookTitleCannotBeBlank", this.title);
    }
    if (!ValidationUtil.validateNotBlank(this.author)) {
      throw new BadRequestException("authorCannotBeBlank", this.author);
    }
    if (!ValidationUtil.validateNotBlank(this.language)) {
      throw new BadRequestException("languageCannotBeBlank", this.language);
    }
    if (!ValidationUtil.validateNotBlank(this.condition)) {
      throw new BadRequestException("conditionCannotBeBlank", this.condition);
    }
    if (this.genres == null || this.genres.isEmpty()) {
      throw new BadRequestException("atLeastOneGenreNeeded", this.genres);
    }
    if (this.coverPhoto != null) {
      ValidationUtil.validateMediaType(coverPhoto);
    }
    if (!ValidationUtil.validateNotBlank(this.ownerId)) {
      throw new BadRequestException("ownerIdCannotBeBlank", this.ownerId);
    }
  }
}
