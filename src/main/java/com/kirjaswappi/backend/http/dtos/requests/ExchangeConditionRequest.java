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
      request.setGenres(node.hasNonNull("genres") ? Arrays.asList(node.get("genres").asText().split(",")) : null);

      if (node.has("books") && node.get("books").isArray()) {
        JsonNode booksNode = node.get("books");
        List<BookRequest> books = new ArrayList<>(booksNode.size()); // Preallocate list

        for (JsonNode bookNode : booksNode) {
          BookRequest bookRequest = new BookRequest();
          bookRequest.setTitle(getJsonNodeAsText(bookNode, "title"));
          bookRequest.setAuthor(getJsonNodeAsText(bookNode, "author"));

          // Extract cover photo
          String coverPhotoData = getJsonNodeAsText(bookNode, "coverPhoto");
          if (coverPhotoData == null || !coverPhotoData.contains(",")) {
            throw new BadRequestException("coverPhotoIsRequiredForExchangeableBook");
          }

          // Extract MIME type and base64-encoded image
          String[] imageParts = coverPhotoData.split(",", 2); // Split once
          String imageInfo = imageParts[0];
          String base64Image = imageParts[1];

          String contentType = extractContentType(imageInfo);

          try {
            MultipartFile coverPhoto = Util.convertBase64ImageToMultipartFile(
                base64Image, "Exchangeable-Book-Cover-Photo.jpg", contentType);
            if (coverPhoto == null) {
              throw new BadRequestException("invalidExchangeableCoverPhoto");
            }
            bookRequest.setCoverPhoto(coverPhoto);
          } catch (IOException ioException) {
            throw new RuntimeException("Error converting base64 to MultipartFile", ioException);
          }
          books.add(bookRequest);
        }
        request.setBooks(books);
      } else {
        request.setBooks(null);
      }

      return request;
    }

    /**
     * Helper method to safely get text from a JSON node.
     */
    private String getJsonNodeAsText(JsonNode node, String fieldName) {
      return node.hasNonNull(fieldName) ? node.get(fieldName).asText() : null;
    }

    /**
     * Extracts MIME type from a base64 data URI.
     */
    private String extractContentType(String imageInfo) {
      String[] parts = imageInfo.split("[,:;]");
      return (parts.length > 1) ? parts[1] : "";
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
