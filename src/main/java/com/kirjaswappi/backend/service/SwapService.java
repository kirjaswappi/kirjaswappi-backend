/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.jpa.daos.SwapRequestDao;
import com.kirjaswappi.backend.jpa.repositories.SwapRequestRepository;
import com.kirjaswappi.backend.mapper.SwapRequestMapper;
import com.kirjaswappi.backend.service.entities.*;
import com.kirjaswappi.backend.service.enums.SwapStatus;
import com.kirjaswappi.backend.service.exceptions.IllegalSwapRequestException;
import com.kirjaswappi.backend.service.exceptions.SwapRequestExistsAlreadyException;

@Service
@Transactional
public class SwapService {
  @Autowired
  private UserService userService;
  @Autowired
  private BookService bookService;
  @Autowired
  private GenreService genreService;
  @Autowired
  private SwapRequestRepository swapRequestRepository;

  public SwapRequest createSwapRequest(SwapRequest swapRequest) {
    // validation: check if the swap request exists already for this book
    if (swapRequestRepository.existsAlready(swapRequest.getSender().getId(),
        swapRequest.getReceiver().getId(), swapRequest.getBookToSwapWith().getId())) {
      throw new SwapRequestExistsAlreadyException();
    }

    // set sender:
    User sender = userService.getUser(swapRequest.getSender().getId());
    swapRequest.setSender(sender);

    // set receiver:
    User receiver = userService.getUser(swapRequest.getReceiver().getId());
    swapRequest.setReceiver(receiver);

    // set bookToSwapWith:
    Book bookToSwapWith = bookService.getBookById(swapRequest.getBookToSwapWith().getId());
    swapRequest.setBookToSwapWith(bookToSwapWith);

    // check if the bookToSwapWith belongs to the receiver:
    if (Optional.ofNullable(receiver.getBooks())
        .stream()
        .flatMap(Collection::stream)
        .noneMatch(book -> book.getId().equals(bookToSwapWith.getId()))) {
      throw new IllegalSwapRequestException("bookToSwapWithDoesNotBelongToReceiver");
    }

    if (swapRequest.getSwapOffer() != null) {
      // set offeredBook if present:
      if (swapRequest.getSwapOffer().getOfferedBook() != null) {
        SwappableBook offeredBook = bookService
            .getSwappableBookById(swapRequest.getSwapOffer().getOfferedBook().getId());

        // check if the offeredBook is present as one of the swappableBooks conditions
        // of bookToSwapWith:
        if (Optional.ofNullable(bookToSwapWith.getSwapCondition().getSwappableBooks())
            .stream()
            .flatMap(Collection::stream)
            .noneMatch(book -> book.getId().equals(offeredBook.getId()))) {
          throw new IllegalSwapRequestException("offeredBookDoesNotBelongToOneOfTheSwappableBooks");
        }

        swapRequest.getSwapOffer().setOfferedBook(offeredBook);
      }

      // set offeredGenre if present:
      if (swapRequest.getSwapOffer().getOfferedGenre() != null) {
        Genre offeredGenre = genreService.getGenreById(swapRequest.getSwapOffer().getOfferedGenre().getId());

        // check if the offeredGenre is present as one of the swappableGenres conditions
        // of bookToSwapWith:
        if (Optional.ofNullable(bookToSwapWith.getSwapCondition().getSwappableGenres())
            .stream()
            .flatMap(Collection::stream)
            .noneMatch(genre -> genre.getId().equals(offeredGenre.getId()))) {
          throw new IllegalSwapRequestException("offeredGenreDoesNotBelongToOneOfTheSwappableGenres");
        }

        swapRequest.getSwapOffer().setOfferedGenre(offeredGenre);
      }
    }

    swapRequest.setSwapStatus(SwapStatus.PENDING);
    SwapRequestDao dao = SwapRequestMapper.toDao(swapRequest);
    SwapRequestDao createdDao = swapRequestRepository.save(dao);
    return SwapRequestMapper.toEntity(createdDao);
  }

  public void deleteAllSwapRequests() {
    swapRequestRepository.deleteAll();
  }
}