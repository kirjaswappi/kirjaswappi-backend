/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAllBooksFilter {
  @Schema(description = "Search parameter to find specific books by name, author, or genre.", example = "Lord of the Rings")
  String search;
  @Schema(description = "Filter parameter for the language of the book.", example = "English", allowableValues = {
      "English", "Bengali", "Hindi", "Spanish", "French", "German", "Russian", "Arabic", "Chinese", "Japanese" })
  String language;
  @Schema(description = "Filter parameter for the condition of the book.", example = "New", allowableValues = {
      "New", "Like New", "Very Good", "Good", "Acceptable" })
  String condition;
  @Schema(description = "Filter parameter for the genre of the book.", example = "Fantasy", allowableValues = {
      "Fantasy", "Science Fiction", "Mystery", "Horror", "Romance", "Thriller", "Historical Fiction", "Non-Fiction" })
  String genre;
}
