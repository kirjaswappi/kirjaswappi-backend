/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers.mockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirjaswappi.backend.http.controllers.SwapController;
import com.kirjaswappi.backend.http.controllers.mockMvc.config.CustomMockMvcConfiguration;
import com.kirjaswappi.backend.http.dtos.requests.CreateSwapRequest;
import com.kirjaswappi.backend.service.SwapService;
import com.kirjaswappi.backend.service.entities.*;
import com.kirjaswappi.backend.service.enums.SwapStatus;
import com.kirjaswappi.backend.service.enums.SwapType;

@WebMvcTest(SwapController.class)
@Import(CustomMockMvcConfiguration.class)
class SwapControllerTest {
  private static final String API_PATH = "/api/v1/swap-requests";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SwapService swapService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Should return OK when creating a valid swap request")
  void shouldReturnOk_whenValidRequest() throws Exception {
    CreateSwapRequest request = new CreateSwapRequest();
    request.setSenderId("user1");
    request.setReceiverId("user2");
    request.setBookIdToSwapWith("book1");
    request.setSwapType("ByBooks");
    request.setAskForGiveaway(true);
    request.setNote("I'd like to swap");

    CreateSwapRequest.SwapOfferRequest offer = new CreateSwapRequest.SwapOfferRequest();
    offer.setOfferedBookId("book2");
    request.setSwapOffer(offer);

    // Setup mocked SwapRequest entity
    SwapRequest entity = new SwapRequest();
    entity.setId("swap-001");

    User sender = new User();
    sender.setId("user1");
    User receiver = new User();
    receiver.setId("user2");
    entity.setSender(sender);
    entity.setReceiver(receiver);

    Book bookToSwapWith = new Book();
    bookToSwapWith.setId("book1");
    bookToSwapWith.setTitle("Book One");
    bookToSwapWith.setAuthor("Author A");
    entity.setBookToSwapWith(bookToSwapWith);

    SwappableBook offeredBook = new SwappableBook();
    offeredBook.setId("book2");
    offeredBook.setTitle("Offered Book");
    offeredBook.setAuthor("Author B");
    offeredBook.setCoverPhoto("http://example.com/cover2.jpg");
    SwapOffer offerEntity = new SwapOffer(offeredBook, null);
    entity.setSwapOffer(offerEntity);

    entity.setSwapType(SwapType.BY_BOOKS);
    entity.setSwapStatus(SwapStatus.PENDING);
    entity.setAskForGiveaway(false);
    entity.setNote("I'd like to swap");
    entity.setRequestedAt(Instant.parse("2024-01-01T00:00:00Z"));
    entity.setUpdatedAt(Instant.parse("2024-01-01T01:00:00Z"));

    when(swapService.createSwapRequest(any())).thenReturn(entity);

    mockMvc.perform(post(API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("swap-001"))
        .andExpect(jsonPath("$.senderId").value("user1"))
        .andExpect(jsonPath("$.receiverId").value("user2"))
        .andExpect(jsonPath("$.bookToSwapWith.id").value("book1"))
        .andExpect(jsonPath("$.bookToSwapWith.title").value("Book One"))
        .andExpect(jsonPath("$.swapType").value("ByBooks"))
        .andExpect(jsonPath("$.swapOffer.offeredBook.id").value("book2"))
        .andExpect(jsonPath("$.swapOffer.offeredBook.title").value("Offered Book"))
        .andExpect(jsonPath("$.askForGiveaway").value(false))
        .andExpect(jsonPath("$.swapStatus").value("Pending"))
        .andExpect(jsonPath("$.note").value("I'd like to swap"))
        .andExpect(jsonPath("$.requestedAt").value("2024-01-01T00:00:00Z"))
        .andExpect(jsonPath("$.updatedAt").value("2024-01-01T01:00:00Z"));
  }

  @Test
  @DisplayName("Should return BadRequest when required fields are missing")
  void shouldReturnBadRequest_whenMissingRequiredFields() throws Exception {
    CreateSwapRequest request = new CreateSwapRequest(); // All fields null

    mockMvc.perform(post(API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return BadRequest when both swap offers are present")
  void shouldReturnBadRequest_whenBothSwapOffersPresent() throws Exception {
    CreateSwapRequest request = new CreateSwapRequest();
    request.setSenderId("user1");
    request.setReceiverId("user2");
    request.setBookIdToSwapWith("book1");
    request.setSwapType("ByGenres");

    CreateSwapRequest.SwapOfferRequest offer = new CreateSwapRequest.SwapOfferRequest();
    offer.setOfferedBookId("book2");
    offer.setOfferedGenreId("genre1");
    request.setSwapOffer(offer);

    mockMvc.perform(post(API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return BadRequest when no swap offer is present")
  void shouldReturnBadRequest_whenNoSwapOfferPresent() throws Exception {
    CreateSwapRequest request = new CreateSwapRequest();
    request.setSenderId("user1");
    request.setReceiverId("user2");
    request.setBookIdToSwapWith("book1");
    request.setSwapType("ByGenres");

    request.setSwapOffer(new CreateSwapRequest.SwapOfferRequest()); // both null

    mockMvc.perform(post(API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return NoContent when deleting all swap requests")
  void shouldReturnNoContent() throws Exception {
    mockMvc.perform(delete(API_PATH))
        .andExpect(status().isNoContent());

    verify(swapService, times(1)).deleteAllSwapRequests();
  }
}
