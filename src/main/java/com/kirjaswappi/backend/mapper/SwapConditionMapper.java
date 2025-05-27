/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.SwapConditionDao;
import com.kirjaswappi.backend.service.entities.SwapCondition;
import com.kirjaswappi.backend.service.enums.SwapType;

@Component
@NoArgsConstructor
public class SwapConditionMapper {
  public static SwapCondition toEntity(SwapConditionDao dao) {
    if (dao == null) {
      return new SwapCondition();
    }
    var entity = new SwapCondition();
    entity.setSwapType(SwapType.fromCode(dao.getSwapType()));
    entity.setGiveAway(dao.isGiveAway());
    entity.setOpenForOffers(dao.isOpenForOffers());
    entity.setSwappableGenres(dao.getSwappableGenres().stream().map(GenreMapper::toEntity).toList());
    entity.setSwappableBooks(dao.getSwappableBooks().stream().map(SwappableBookMapper::toEntity).toList());
    return entity;
  }

  public static SwapConditionDao toDao(SwapCondition entity) {
    var dao = new SwapConditionDao();
    // Defensive: ensure swapType is not null
    if (entity.getSwapType() == null) {
      entity.setSwapType(com.kirjaswappi.backend.service.enums.SwapType.BY_BOOKS);
    }
    if (entity.getSwappableGenres() == null) {
      entity.setSwappableGenres(java.util.List.of());
    }
    if (entity.getSwappableBooks() == null) {
      entity.setSwappableBooks(java.util.List.of());
    }
    dao.setSwapType(entity.getSwapType().getCode());
    dao.setGiveAway(entity.isGiveAway());
    dao.setOpenForOffers(entity.isOpenForOffers());
    dao.setSwappableGenres(entity.getSwappableGenres().stream().map(GenreMapper::toDao).toList());
    dao.setSwappableBooks(entity.getSwappableBooks().stream().map(SwappableBookMapper::toDao).toList());
    return dao;
  }
}
