/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.kirjaswappi.backend.jpa.repositories.SwapRequestRepository;
import com.kirjaswappi.backend.service.entities.Book;
import com.kirjaswappi.backend.service.entities.SwapRequest;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.SwapRequestExistsAlreadyException;

class SwapServiceTest {
  @Mock
  private SwapRequestRepository swapRequestRepository;
  @InjectMocks
  private SwapService swapService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should throw SwapRequestExistsAlreadyException when swap request already exists")
  void createSwapRequestThrowsWhenExistsAlready() {
    var swapRequest = new SwapRequest();
    var sender = new User();
    sender.setId("senderId");
    var receiver = new User();
    receiver.setId("receiverId");
    var book = new Book();
    book.setId("bookId");
    swapRequest.setSender(sender);
    swapRequest.setReceiver(receiver);
    swapRequest.setBookToSwapWith(book);
    when(swapRequestRepository.existsAlready("senderId", "receiverId", "bookId")).thenReturn(true);
    assertThrows(SwapRequestExistsAlreadyException.class, () -> swapService.createSwapRequest(swapRequest));
  }

  // Add more tests for getSwapRequests, updateSwapRequest, deleteSwapRequest,
  // etc.
}
