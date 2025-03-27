/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.kirjaswappi.backend.service.entities.ExchangeCondition;
import com.kirjaswappi.backend.service.entities.ExchangeableBook;
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
@JsonDeserialize(using = ExchangeConditionRequest.ExchangeConditionRequestDeserializer.class)
public class ExchangeConditionRequest {
  @Schema(description = "The open for offers status of the exchange condition.", example = "true", requiredMode = REQUIRED)
  private boolean openForOffers;

  @Schema(description = "The genres of the exchange condition.", example = "[\"Fiction\"]", requiredMode = REQUIRED)
  private List<String> genres;

  @Schema(description = "The books of the exchange condition.", example = "[{\"title\": \"The Alchemist\", \"author\": \"Paulo Coelho\", \"coverPhoto\": \"book-cover-photo.jpg\"}]", requiredMode = REQUIRED)
  private List<BookRequest> books;

  public ExchangeCondition toEntity() {
    if (this.genres == null)
      genres = List.of();
    if (this.books == null)
      books = List.of();
    List<Genre> exchangeableGenres = this.genres.stream().map(Genre::new).toList();
    List<ExchangeableBook> exchangeableBooks = this.books.stream().map(BookRequest::toEntity).toList();
    return new ExchangeCondition(openForOffers, exchangeableGenres, exchangeableBooks);
  }

  static class ExchangeConditionRequestDeserializer extends JsonDeserializer<ExchangeConditionRequest> {
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
          MultipartFile coverPhoto;
          try {
            coverPhoto = (MultipartFile) bookNode.get("coverPhoto");
          } catch (Exception e) {
            byte[] coverPhotoBytes = bookNode.get("coverPhoto").binaryValue(); // Extract binary data

            if (coverPhotoBytes == null || coverPhotoBytes.length == 0) {
              throw new BadRequestException("coverPhotoIsRequiredForExchangeableBook");
            } else {
              try {
                // Convert byte array to Multipart File
                coverPhoto = Util.convertByteArrayToMultipartFile(coverPhotoBytes, "cover.jpg", "image/jpeg");
                if (coverPhoto == null) {
                  throw new BadRequestException("coverPhotoIsInvalid");
                }
              } catch (IOException ioException) {
                throw new RuntimeException("Error converting byte array to MultipartFile", ioException);
              }
            }
          }
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

  @Getter
  @Setter
  public static class BookRequest {
    @Schema(description = "The title of the book.", example = "The Alchemist", requiredMode = NOT_REQUIRED)
    private String title;

    @Schema(description = "The author of the book.", example = "Paulo Coelho", requiredMode = NOT_REQUIRED)
    private String author;

    @Schema(description = "The cover photo of the book.", requiredMode = NOT_REQUIRED)
    private MultipartFile coverPhoto;

    public ExchangeableBook toEntity() {
      var book = new ExchangeableBook();
      book.setTitle(title);
      book.setAuthor(author);
      book.setCoverPhotoFile(coverPhoto);
      return book;
    }
  }

  public void validateOpenForOffers() {
    if (this.getGenres() != null && !this.getGenres().isEmpty()) {
      throw new BadRequestException("exchangeableGenreCannotBePresent", this.getGenres());
    }
    if (this.getBooks() != null && !this.getBooks().isEmpty()) {
      throw new BadRequestException("exchangeableBookCannotBePresent", this.getBooks());
    }
  }

  public void validateNotOpenForOffers() {
    if ((this.getGenres() == null || this.getGenres().isEmpty()) &&
        (this.getBooks() == null || this.getBooks().isEmpty())) {
      throw new BadRequestException("atLeastOneExchangeConditionIsNeeded", this.getGenres());
    }

    if (this.getGenres() != null && !this.getGenres().isEmpty()) {
      this.getGenres().forEach(genre -> {
        if (!ValidationUtil.validateNotBlank(genre)) {
          throw new BadRequestException("genreCannotBeBlankForExchangeCondition", genre);
        }
      });
    }

    if (this.getBooks() != null && !this.getBooks().isEmpty()) {
      this.getBooks().forEach(book -> {
        if (!ValidationUtil.validateNotBlank(book.getTitle())) {
          throw new BadRequestException("bookTitleCannotBeBlankForExchangeableBook", book.getTitle());
        }
        if (!ValidationUtil.validateNotBlank(book.getAuthor())) {
          throw new BadRequestException("authorCannotBeBlankForExchangeableBook", book.getAuthor());
        }
        if (book.getCoverPhoto() == null) {
          throw new BadRequestException("coverPhotoIsRequiredForExchangeableBook");
        }
        ValidationUtil.validateMediaType(book.getCoverPhoto());
      });
    }
  }
}
