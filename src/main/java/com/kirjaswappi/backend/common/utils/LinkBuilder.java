/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

@UtilityClass
public class LinkBuilder {

  public static final String PAGE = "page";
  public static final String SIZE = "size";

  public static <T> PagedModel<T> forPage(Page<T> pageOfSomething, String endpoint) {

    // Creation of PageModel
    PagedModel<T> model = PagedModel.of(pageOfSomething.getContent(),
        new PagedModel.PageMetadata(pageOfSomething.getSize(), pageOfSomething.getNumber(),
            pageOfSomething.getTotalElements(), pageOfSomething.getTotalPages()));

    // Self link
    List<Link> linkList = new ArrayList<>();
    linkList.add(Link.of(endpoint, "self"));

    var size = Integer.toString(pageOfSomething.getSize());

    // Link to next page
    if (pageOfSomething.hasNext()) {
      var nextPage = Integer.toString(pageOfSomething.nextPageable().getPageNumber());
      linkList.add(Link.of(endpoint + "?" + PAGE + "=" + nextPage + "&" + SIZE + "=" + size, "next"));
    }

    // Link to prev page
    if (!pageOfSomething.isFirst()) {
      var prevPage = Integer.toString(pageOfSomething.previousPageable().getPageNumber());
      linkList.add(Link.of(endpoint + "?" + PAGE + "=" + prevPage + "&" + SIZE + "=" + size, "prev"));
    }

    model.add(linkList);
    return model;
  }
}
