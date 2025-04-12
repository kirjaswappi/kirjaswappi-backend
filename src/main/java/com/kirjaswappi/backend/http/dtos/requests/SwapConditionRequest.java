/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

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
import com.kirjaswappi.backend.service.entities.Genre;
import com.kirjaswappi.backend.service.entities.SwapCondition;
import com.kirjaswappi.backend.service.entities.SwappableBook;
import com.kirjaswappi.backend.service.enums.SwapConditionType;
import com.kirjaswappi.backend.service.exceptions.BadRequestException;

@Getter
@Setter
@JsonDeserialize(using = SwapConditionRequest.SwapConditionRequestDeserializer.class)
public class SwapConditionRequest {
  @Schema(description = "The condition type for swapping the book.", example = "GiveAway, OpenForOffers, ByGenres, ByBooks")
  private String conditionType;

  @Schema(description = "To give away the book for free.", example = "true")
  private boolean giveAway;

  @Schema(description = "The user is open for offers for swapping the book.", example = "true")
  private boolean openForOffers;

  @Schema(description = "The acceptable genres of books to swap with.", example = "[\"Fiction\"]")
  private List<String> genres;

  @Schema(description = "The exact books to swap with.", example = "[{\"title\": \"The Alchemist\", \"author\": \"Paulo Coelho\", \"coverPhoto\": \"book-cover-photo.jpg\"}]")
  private List<BookRequest> books;

  public SwapCondition toEntity() {
    SwapConditionType conditionTypeCode = SwapConditionType.fromCode(conditionType);
    if (this.genres == null)
      genres = List.of();
    if (this.books == null)
      books = List.of();
    List<Genre> swappableGenres = this.genres.stream().map(Genre::new).toList();
    List<SwappableBook> swappableBooks = this.books.stream().map(BookRequest::toEntity).toList();

    checkIfOnlyOneOfTheSwapConditionIsProvided(conditionTypeCode, giveAway,
        openForOffers, swappableGenres, swappableBooks);

    validateSwapCondition();

    return new SwapCondition(conditionTypeCode, giveAway, openForOffers,
        swappableGenres, swappableBooks);
  }

  private static void checkIfOnlyOneOfTheSwapConditionIsProvided(SwapConditionType conditionType,
      boolean giveAway,
      boolean openForOffers,
      List<Genre> swappableGenres,
      List<SwappableBook> swappableBooks) {
    boolean isGenresSet = swappableGenres != null && !swappableGenres.isEmpty();
    boolean isBooksSet = swappableBooks != null && !swappableBooks.isEmpty();

    switch (conditionType) {
    case GIVE_AWAY:
      if (!giveAway || openForOffers || isGenresSet || isBooksSet) {
        throw new BadRequestException("onlyOneSwapConditionMustBeSet", "giveAway");
      }
      break;
    case OPEN_FOR_OFFERS:
      if (!openForOffers || giveAway || isGenresSet || isBooksSet) {
        throw new BadRequestException("onlyOneSwapConditionMustBeSet", "openForOffers");
      }
      break;
    case BY_GENRES:
      if (!isGenresSet || giveAway || openForOffers || isBooksSet) {
        throw new BadRequestException("onlyOneSwapConditionMustBeSet", "genres");
      }
      break;
    case BY_BOOKS:
      if (!isBooksSet || giveAway || openForOffers || isGenresSet) {
        throw new BadRequestException("onlyOneSwapConditionMustBeSet", "books");
      }
      break;
    default:
      throw new BadRequestException("Unsupported condition type");
    }
  }

  static class SwapConditionRequestDeserializer extends JsonDeserializer<SwapConditionRequest> {
    @Override
    public SwapConditionRequest deserialize(JsonParser jsonParser, DeserializationContext context)
        throws IOException {
      ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
      JsonNode node = mapper.readTree(jsonParser);

      if (node.isTextual()) {
        node = mapper.readTree(node.asText());
      }
      SwapConditionRequest request = new SwapConditionRequest();
      request.setConditionType(node.get("conditionType").asText());
      request.setGiveAway(node.get("giveAway").asBoolean());
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
            throw new BadRequestException("coverPhotoIsRequiredForSwappableBook");
          }

          // Extract MIME type and base64-encoded image
          String[] imageParts = coverPhotoData.split(",", 2); // Split once
          String imageInfo = imageParts[0];
          String base64Image = imageParts[1];

          String contentType = extractContentType(imageInfo);

          try {
            MultipartFile coverPhoto = Util.convertBase64ImageToMultipartFile(
                base64Image, "Swappable-Book-Cover-Photo.jpg", contentType);
            if (coverPhoto == null) {
              throw new BadRequestException("invalidSwappableBookCoverPhoto");
            }
            bookRequest.setCoverPhoto(coverPhoto);
          } catch (IOException ioException) {
            throw new RuntimeException("Error: converting base64 to MultipartFile", ioException);
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
    @Schema(description = "The title of the book.", example = "The Alchemist")
    private String title;

    @Schema(description = "The author of the book.", example = "Paulo Coelho")
    private String author;

    @Schema(description = "The cover photo of the book.")
    private MultipartFile coverPhoto;

    public SwappableBook toEntity() {
      var book = new SwappableBook();
      book.setTitle(title);
      book.setAuthor(author);
      book.setCoverPhotoFile(coverPhoto);
      return book;
    }
  }

  public void validateSwapCondition() {
    if (this.getGenres() != null && !this.getGenres().isEmpty()) {
      validateSwappableGenres();
    }

    if (this.getBooks() != null && !this.getBooks().isEmpty()) {
      validateSwappableBooks();
    }
  }

  private void validateSwappableBooks() {
    this.getBooks().forEach(book -> {
      if (!ValidationUtil.validateNotBlank(book.getTitle())) {
        throw new BadRequestException("bookTitleCannotBeBlankForSwappableBook", book.getTitle());
      }
      if (!ValidationUtil.validateNotBlank(book.getAuthor())) {
        throw new BadRequestException("authorCannotBeBlankForSwappableBook", book.getAuthor());
      }
      if (book.getCoverPhoto() == null) {
        throw new BadRequestException("coverPhotoIsRequiredForSwappableBook");
      }
      ValidationUtil.validateMediaType(book.getCoverPhoto());
    });
  }

  private void validateSwappableGenres() {
    this.genres.forEach(genre -> {
      if (!ValidationUtil.validateNotBlank(genre)) {
        throw new BadRequestException("genreCannotBeBlankForSwappableCondition", genre);
      }
    });
  }
}
