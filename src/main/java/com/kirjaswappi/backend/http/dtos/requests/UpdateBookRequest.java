/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.enums.Condition;
import com.kirjaswappi.backend.service.enums.Language;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class UpdateBookRequest {
  @Schema(description = "The ID of the book.", example = "123456", requiredMode = REQUIRED)
  private String id;

  @Schema(description = "The title of the book.", example = "The Alchemist", requiredMode = REQUIRED)
  private String title;

  @Schema(description = "The author of the book.", example = "Paulo Coelho", requiredMode = REQUIRED)
  private String author;

  @Schema(description = "The description of the book.", example = "A novel by Paulo Coelho", requiredMode = NOT_REQUIRED)
  private String description;

  @Schema(description = "The language of the book.", example = "English", requiredMode = REQUIRED)
  private String language;

  @Schema(description = "The condition of the book.", example = "New", requiredMode = REQUIRED)
  private String condition;

  @Schema(description = "The genres of the book.", example = "[\"Fiction\"]", requiredMode = REQUIRED)
  private List<String> genres;

  @Schema(description = "The cover photo of the book.", example = "book-cover-photo.jpg", requiredMode = REQUIRED)
  private MultipartFile coverPhoto;

  @Schema(description = "The exchange condition of the book.", requiredMode = REQUIRED)
  @JsonDeserialize(using = ExchangeConditionRequest.ExchangeConditionRequestDeserializer.class)
  private ExchangeConditionRequest exchangeCondition;

  public void setExchangeCondition(String exchangeConditionJson) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      this.exchangeCondition = objectMapper.readValue(exchangeConditionJson, ExchangeConditionRequest.class);
    } catch (Exception e) {
      throw new BadRequestException("Invalid exchange condition JSON", exchangeConditionJson);
    }
  }

  public Book toEntity() {
    this.validateProperties();
    var book = new Book();
    book.setId(id);
    book.setTitle(title);
    book.setAuthor(author);
    book.setDescription(description);
    book.setLanguage(Language.fromCode(language));
    book.setCondition(Condition.fromCode(condition));
    book.setGenres(this.genres.stream().map(Genre::new).toList());
    book.setCoverPhotoFile(coverPhoto);
    book.setExchangeCondition(exchangeCondition.toEntity());
    return book;
  }

  private void validateProperties() {
    if (!ValidationUtil.validateNotBlank(this.id)) {
      throw new BadRequestException("bookIdCannotBeBlank", this.id);
    }
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
    if (this.coverPhoto == null) {
      throw new BadRequestException("coverPhotoIsRequired");
    }
    ValidationUtil.validateMediaType(coverPhoto);

    if (Objects.isNull(this.exchangeCondition)) {
      throw new BadRequestException("exchangeConditionIsRequired");
    }

    if (this.exchangeCondition.isOpenForOffers()) {
      exchangeCondition.validateOpenForOffers();
    } else {
      exchangeCondition.validateNotOpenForOffers();
    }
  }
}
