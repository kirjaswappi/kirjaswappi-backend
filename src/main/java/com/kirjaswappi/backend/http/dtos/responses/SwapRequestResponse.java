/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.responses;

import java.time.Instant;

import javax.swing.*;

import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entities.SwapOffer;
import com.kirjaswappi.backend.service.entities.SwapRequest;
import com.kirjaswappi.backend.service.entities.SwappableBook;

@Getter
@Setter
public class SwapRequestResponse {
  private String id;
  private String senderId;
  private String receiverId;
  private BookResponse bookToSwapWith;
  private String swapType;
  private SwapOfferResponse swapOffer;
  private boolean askForGiveaway;
  private String swapStatus;
  private String note;
  private Instant requestedAt;
  private Instant updatedAt;

  public SwapRequestResponse(SwapRequest entity) {
    this.id = entity.getId();
    this.senderId = entity.getSender().getId();
    this.receiverId = entity.getReceiver().getId();
    this.bookToSwapWith = new BookResponse(entity.getBookToSwapWith());
    this.swapType = entity.getSwapType().getCode();
    this.swapOffer = entity.getSwapOffer() == null ? null : new SwapOfferResponse(entity.getSwapOffer());
    this.askForGiveaway = entity.isAskForGiveaway();
    this.swapStatus = entity.getSwapStatus().getCode();
    this.note = entity.getNote();
    this.requestedAt = entity.getRequestedAt();
    this.updatedAt = entity.getUpdatedAt();
  }

  @Setter
  @Getter
  static class SwapOfferResponse {
    private OfferedBookResponse offeredBook;
    private GenreResponse offeredGenre;

    public SwapOfferResponse(SwapOffer entity) {
      this.offeredBook = entity.getOfferedBook() == null ? null : new OfferedBookResponse(entity.getOfferedBook());
      this.offeredGenre = entity.getOfferedGenre() == null ? null : new GenreResponse(entity.getOfferedGenre());
    }

    @Setter
    @Getter
    static class OfferedBookResponse {
      private String id;
      private String title;
      private String author;
      private String coverPhotoUrl;

      public OfferedBookResponse(SwappableBook entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
        this.coverPhotoUrl = entity.getCoverPhoto();
      }
    }
  }
}
