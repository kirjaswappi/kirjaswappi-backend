/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import com.kirjaswappi.backend.service.entities.*;
import com.kirjaswappi.backend.service.enums.SwapType;

@Getter
@Setter
public class CreateSwapRequest {
  @Schema(description = "The ID of the sender of swap request.", example = "123456")
  private String senderId;

  @Schema(description = "The ID of the receiver of swap request.", example = "123456")
  private String receiverId;

  @Schema(description = "The ID of the book a user want to swap with.", example = "123456")
  private String bookIdToSwapWith;

  @Schema(description = "The ID of the receiver of swap request.", example = "123456")
  private String swapType;

  private SwapOfferRequest swapOffer;

  @Schema(description = "Ask to give away the book.", example = "true")
  private boolean askForGiveaway;

  @Schema(description = "Add personal note to request.", example = "I want this book, how can I get it?")
  private String note;

  public SwapRequest toEntity() {
    var entity = new SwapRequest();
    var sender = new User();
    sender.setId(this.senderId);
    entity.setSender(sender);
    var receiver = new User();
    receiver.setId(this.receiverId);
    entity.setReceiver(receiver);
    var bookToSwapWith = new Book();
    bookToSwapWith.setId(this.bookIdToSwapWith);
    entity.setBookToSwapWith(bookToSwapWith);
    entity.setSwapType(SwapType.fromCode(this.swapType));
    entity.setSwapOffer(this.swapOffer == null ? null : this.swapOffer.toEntity());
    entity.setAskForGiveaway(this.askForGiveaway);
    entity.setNote(this.note);
    return entity;
  }

  @Getter
  @Setter
  private static class SwapOfferRequest {
    @Schema(description = "The ID of the book offered for swap request.", example = "123456")
    private String offeredBook;

    @Schema(description = "The ID of the genre offered for swap request.", example = "123456")
    private String offeredGenre;

    public SwapOffer toEntity() {
      if (this.offeredBook == null) {
        return new SwapOffer(null, new Genre(this.offeredGenre, null));
      }
      var offeredBook = new SwappableBook();
      offeredBook.setId(this.offeredBook);
      return new SwapOffer(offeredBook, null);
    }
  }
}
