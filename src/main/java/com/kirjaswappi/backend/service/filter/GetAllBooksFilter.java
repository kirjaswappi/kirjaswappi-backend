/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.filter;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.mongodb.core.query.Criteria;

@Getter
@Setter
public class GetAllBooksFilter {
  @Schema(description = "Search parameter to find specific books by name, author, or genre.", example = "Lord of the Rings")
  String search;
  @Schema(description = "Filter parameter for the language of the book.", example = "English", allowableValues = {
      "English", "Bengali", "Hindi", "Spanish", "French", "German", "Russian", "Arabic", "Chinese", "Japanese" })
  String language;
  @Schema(description = "Filter parameter for the condition of the book.", example = "New", allowableValues = {
      "New", "Like New", "Good", "Fair", "Poor" })
  String condition;
  @Schema(description = "Filter parameter for the genre of the book.", example = "Fantasy", allowableValues = {
      "Fantasy", "Science Fiction", "Mystery", "Horror", "Romance", "Thriller", "Historical Fiction", "Non-Fiction" })
  String genre;

  public Criteria buildSearchAndFilterCriteria() {
    List<Criteria> combinedCriteria = new ArrayList<>();

    // Add search criteria:
    if (search != null && !search.isEmpty()) {
      combinedCriteria.add(new Criteria().orOperator(
          Criteria.where("title").regex(search, "i"),
          Criteria.where("author").regex(search, "i"),
          Criteria.where("description").regex(search, "i")));
    }

    // Add filter criteria:
    if (language != null && !language.isEmpty()) {
      combinedCriteria.add(Criteria.where("language").is(language));
    }

    if (condition != null && !condition.isEmpty()) {
      combinedCriteria.add(Criteria.where("condition").is(condition));
    }

    if (genre != null && !genre.isEmpty()) {
      combinedCriteria.add(Criteria.where("genres.name").is(genre));
    }

    var criteria = new Criteria();
    if (combinedCriteria.isEmpty()) {
      return criteria;
    }
    return criteria.andOperator(combinedCriteria.toArray(new Criteria[0]));
  }
}
