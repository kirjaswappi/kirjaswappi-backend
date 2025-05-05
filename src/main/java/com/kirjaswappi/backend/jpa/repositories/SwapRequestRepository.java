/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.jpa.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.kirjaswappi.backend.jpa.daos.SwapRequestDao;

public interface SwapRequestRepository extends MongoRepository<SwapRequestDao, String> {
  @Query(value = "{ 'sender.id': ?0, 'receiver.id': ?1, 'bookToSwapWith.id': ?2 }", exists = true)
  boolean existsAlready(String senderId, String receiverId, String bookToSwapWithId);
}
