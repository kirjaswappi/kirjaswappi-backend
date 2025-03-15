/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.mapper;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

import com.kirjaswappi.backend.jpa.daos.ExchangeConditionDao;
import com.kirjaswappi.backend.service.entities.ExchangeCondition;

@Component
@NoArgsConstructor
public class ExchangeConditionMapper {
  public static ExchangeCondition toEntity(ExchangeConditionDao dao) {
    if (dao == null) {
      return new ExchangeCondition();
    }
    var entity = new ExchangeCondition();
    entity.setOpenForOffers(dao.isOpenForOffers());
    entity.setExchangeableGenres(dao.getExchangeableGenres().stream().map(GenreMapper::toEntity).toList());
    entity
        .setExchangeableBooks(dao.getExchangeableBooks().stream().map(ExchangeableBookMapper::toEntity).toList());
    return entity;
  }

  public static ExchangeConditionDao toDao(ExchangeCondition entity) {
    var dao = new ExchangeConditionDao();
    dao.setOpenForOffers(entity.isOpenForOffers());
    dao.setExchangeableGenres(entity.getExchangeableGenres().stream().map(GenreMapper::toDao).toList());
    dao.setExchangeableBooks(entity.getExchangeableBooks().stream().map(ExchangeableBookMapper::toDao).toList());
    return dao;
  }
}
