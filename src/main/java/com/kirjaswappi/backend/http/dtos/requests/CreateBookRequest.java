/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kirjaswappi.backend.common.utils.Util;
import com.kirjaswappi.backend.http.validations.ValidationUtil;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.ExchangeCondition;
import com.kirjaswappi.backend.service.entities.ExchangeableBook;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.enums.Condition;
import com.kirjaswappi.backend.service.enums.Language;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
public class CreateBookRequest {
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

  @Schema(description = "The cover photo of the book.", requiredMode = REQUIRED)
  private MultipartFile coverPhoto;

  @Schema(description = "The user ID of the book owner.", example = "123456", requiredMode = REQUIRED)
  private String ownerId;

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
    book.setTitle(title);
    book.setAuthor(author);
    book.setDescription(description);
    book.setLanguage(Language.fromCode(language));
    book.setCondition(Condition.fromCode(condition));
    book.setGenres(this.genres.stream().map(Genre::new).toList());
    book.setCoverPhotoFile(coverPhoto);
    var user = new User();
    user.setId(ownerId);
    book.setOwner(user);
    book.setExchangeCondition(exchangeCondition.toEntity());
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
    if (this.coverPhoto == null) {
      throw new BadRequestException("coverPhotoIsRequired", this.coverPhoto);
    }
    ValidationUtil.validateMediaType(coverPhoto);

    if (!ValidationUtil.validateNotBlank(this.ownerId)) {
      throw new BadRequestException("ownerIdCannotBeBlank", this.ownerId);
    }
    if (Objects.isNull(this.exchangeCondition)) {
      throw new BadRequestException("exchangeConditionIsRequired", this.exchangeCondition);
    }

    if (this.exchangeCondition.openForOffers) {
      validateOpenForOffers();
    } else {
      validateNotOpenForOffers();
    }
  }

  private void validateOpenForOffers() {
    if (this.exchangeCondition.genres != null && !this.exchangeCondition.genres.isEmpty()) {
      throw new BadRequestException("exchangeableGenreCannotBePresent", this.exchangeCondition.genres);
    }
    if (this.exchangeCondition.books != null && !this.exchangeCondition.books.isEmpty()) {
      throw new BadRequestException("exchangeableBookCannotBePresent", this.exchangeCondition.books);
    }
  }

  private void validateNotOpenForOffers() {
    if ((this.exchangeCondition.genres == null || this.exchangeCondition.genres.isEmpty()) &&
        (this.exchangeCondition.books == null || this.exchangeCondition.books.isEmpty())) {
      throw new BadRequestException("atLeastOneExchangeConditionIsNeeded", this.exchangeCondition.genres);
    }

    if (this.exchangeCondition.genres != null && !this.exchangeCondition.genres.isEmpty()) {
      this.exchangeCondition.genres.forEach(genre -> {
        if (!ValidationUtil.validateNotBlank(genre)) {
          throw new BadRequestException("genreCannotBeBlankForExchangeCondition", genre);
        }
      });
    }

    if (this.exchangeCondition.books != null && !this.exchangeCondition.books.isEmpty()) {
      this.exchangeCondition.books.forEach(book -> {
        if (!ValidationUtil.validateNotBlank(book.title)) {
          throw new BadRequestException("bookTitleCannotBeBlankForExchangeableBook", book.title);
        }
        if (!ValidationUtil.validateNotBlank(book.author)) {
          throw new BadRequestException("authorCannotBeBlankForExchangeableBook", book.author);
        }
        if (book.coverPhoto == null) {
          throw new BadRequestException("coverPhotoIsRequiredForExchangeableBook", book.coverPhoto);
        }
        ValidationUtil.validateMediaType(book.coverPhoto);
      });
    }
  }

  @Getter
  @Setter
  private static class ExchangeConditionRequest {
    @Schema(description = "The open for offers status of the exchange condition.", example = "true", requiredMode = REQUIRED)
    private Boolean openForOffers;

    @Schema(description = "The genres of the exchange condition.", example = "[\"Fiction\"]", requiredMode = REQUIRED)
    private List<String> genres;

    @Schema(description = "The books of the exchange condition.", example = "[\"123456\"]", requiredMode = REQUIRED)
    private List<BookRequest> books;

    public ExchangeCondition toEntity() {
      List<Genre> exchangeableGenres = this.genres.stream().map(Genre::new).toList();
      List<ExchangeableBook> exchangeableBooks = this.books.stream().map(BookRequest::toEntity).toList();
      return new ExchangeCondition(openForOffers, exchangeableGenres, exchangeableBooks);
    }

    public static class ExchangeConditionRequestDeserializer extends JsonDeserializer<ExchangeConditionRequest> {
      @Override
      public ExchangeConditionRequest deserialize(JsonParser jsonParser, DeserializationContext context)
          throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);
        if (node.isTextual()) {
          node = mapper.readTree(node.asText());
        }
        ExchangeConditionRequest request = new ExchangeConditionRequest();
        request.setOpenForOffers(node.get("openForOffers").asBoolean());
        request.setGenres(node.get("genres").isNull() ? null : Arrays.asList(node.get("genres").asText().split(",")));
        if (node.get("books").isArray()) {
          List<BookRequest> books = new ArrayList<>();
          for (JsonNode bookNode : node.get("books")) {
            BookRequest bookRequest = new BookRequest();
            bookRequest.setTitle(bookNode.get("title").asText());
            bookRequest.setAuthor(bookNode.get("author").asText());
            String coverPhotoBase64 = bookNode.get("coverPhoto").asText();
            MultipartFile coverPhoto = Util.base64ToMultipartFile(coverPhotoBase64, "coverPhoto.jpg");
            bookRequest.setCoverPhoto(coverPhoto);
            books.add(bookRequest);
          }
          request.setBooks(books);
        } else {
          request.setBooks(null);
        }
        return request;
      }
    }
  }

  @Getter
  @Setter
  private static class BookRequest {
    @Schema(description = "The title of the book.", example = "The Alchemist", requiredMode = REQUIRED)
    private String title;

    @Schema(description = "The author of the book.", example = "Paulo Coelho", requiredMode = REQUIRED)
    private String author;

    @Schema(description = "The cover photo of the book.", requiredMode = REQUIRED)
    private MultipartFile coverPhoto;

    public ExchangeableBook toEntity() {
      var book = new ExchangeableBook();
      book.setTitle(title);
      book.setAuthor(author);
      book.setCoverPhotoFile(coverPhoto);
      return book;
    }
  }
}
