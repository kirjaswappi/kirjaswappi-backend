/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import java.util.List;

import com.kirjaswappi.backend.jpa.daos.BookDao;
import com.kirjaswappi.backend.service.filters.GetAllBooksFilter;

public interface CustomBookRepository {
  List<BookDao> findAllBooksByFilter(GetAllBooksFilter filter);
}
