/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.SwapOfferDao;
import com.kirjaswappi.backend.service.entities.SwapOffer;

@Component
@NoArgsConstructor
public class SwapOfferMapper {
  public static SwapOffer toEntity(SwapOfferDao dao) {
    var entity = new SwapOffer();
    if (dao.getOfferedBook() != null) {
      entity.setOfferedBook(SwappableBookMapper.toEntity(dao.getOfferedBook()));
    }
    if (dao.getOfferedGenre() != null) {
      entity.setOfferedGenre(GenreMapper.toEntity(dao.getOfferedGenre()));
    }
    return entity;
  }

  public static SwapOfferDao toDao(SwapOffer entity) {
    var dao = new SwapOfferDao();
    if (entity.getOfferedBook() != null) {
      dao.setOfferedBook(SwappableBookMapper.toDao(entity.getOfferedBook()));
    }
    if (entity.getOfferedGenre() != null) {
      dao.setOfferedGenre(GenreMapper.toDao(entity.getOfferedGenre()));
    }
    return dao;
  }
}
