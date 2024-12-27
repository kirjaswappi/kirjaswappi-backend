/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.BY_EMAIL;
import static com.kirjaswappi.backend.common.utils.Constants.BY_ID;
import static com.kirjaswappi.backend.common.utils.Constants.COVER_PHOTO;
import static com.kirjaswappi.backend.common.utils.Constants.EMAIL;
import static com.kirjaswappi.backend.common.utils.Constants.ID;
import static com.kirjaswappi.backend.common.utils.Constants.PHOTOS;
import static com.kirjaswappi.backend.common.utils.Constants.PROFILE_PHOTO;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.http.dtos.requests.CreatePhotoRequest;
import com.kirjaswappi.backend.service.PhotoService;

@RestController
@RequestMapping(API_BASE + PHOTOS)
@Validated
public class PhotoController {
  @Autowired
  private PhotoService photoService;

  @PostMapping(PROFILE_PHOTO)
  @Operation(summary = "Add a profile photo.", description = "Add a profile photo to a user.", responses = {
      @ApiResponse(responseCode = "200", description = "Profile Photo Added.") })
  public ResponseEntity<String> addProfilePhoto(@Valid @ModelAttribute CreatePhotoRequest request) throws Exception {
    return ResponseEntity.ok(photoService.addProfilePhoto(request.getUserId(), request.getImage()));
  }

  @PostMapping(COVER_PHOTO)
  @Operation(summary = "Add a cover photo.", description = "Add a cover photo to a user.", responses = {
      @ApiResponse(responseCode = "200", description = "Cover Photo Added.") })
  public ResponseEntity<String> addCoverPhoto(@Valid @ModelAttribute CreatePhotoRequest request) throws Exception {
    return ResponseEntity.ok(photoService.addCoverPhoto(request.getUserId(), request.getImage()));
  }

  @DeleteMapping(PROFILE_PHOTO + ID)
  @Operation(summary = "Delete a profile photo.", description = "Delete a profile photo of a user.", responses = {
      @ApiResponse(responseCode = "204", description = "Profile Photo Deleted.") })
  public ResponseEntity<Void> deleteProfilePhoto(@Parameter(description = "User Id.") @PathVariable String id)
      throws Exception {
    photoService.deleteProfilePhoto(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(COVER_PHOTO + ID)
  @Operation(summary = "Delete a cover photo.", description = "Delete a cover photo of a user.", responses = {
      @ApiResponse(responseCode = "204", description = "Cover Photo Deleted.") })
  public ResponseEntity<Void> deleteCoverPhoto(@Parameter(description = "User Id.") @PathVariable String id)
      throws Exception {
    photoService.deleteCoverPhoto(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(PROFILE_PHOTO + BY_EMAIL + EMAIL)
  @Operation(summary = "Get a profile photo by email.", description = "Get a profile photo of a user by email.", responses = {
      @ApiResponse(responseCode = "200", description = "Profile Photo Found.") })
  public ResponseEntity<String> getProfilePhotoByEmail(
      @Parameter(description = "User Email.") @PathVariable String email) throws Exception {
    return ResponseEntity.ok(photoService.getPhotoByUserEmail(email, true));
  }

  @GetMapping(COVER_PHOTO + BY_EMAIL + EMAIL)
  @Operation(summary = "Get a cover photo by email.", description = "Get a cover photo of a user by email.", responses = {
      @ApiResponse(responseCode = "200", description = "Cover Photo Found.") })
  public ResponseEntity<String> getCoverPhotoByEmail(
      @Parameter(description = "User Email.") @PathVariable String email) throws Exception {
    return ResponseEntity.ok(photoService.getPhotoByUserEmail(email, false));
  }

  @GetMapping(PROFILE_PHOTO + BY_ID + ID)
  @Operation(summary = "Get a profile photo by user id.", description = "Get a profile photo of a user by user id.", responses = {
      @ApiResponse(responseCode = "200", description = "Profile Photo Found.") })
  public ResponseEntity<String> getProfilePhotoById(@Parameter(description = "User Id.") @PathVariable String id)
      throws Exception {
    return ResponseEntity.ok(photoService.getPhotoByUserId(id, true));
  }

  @GetMapping(COVER_PHOTO + BY_ID + ID)
  @Operation(summary = "Get a cover photo by user id.", description = "Get a cover photo of a user by user id.", responses = {
      @ApiResponse(responseCode = "200", description = "Cover Photo Found.") })
  public ResponseEntity<String> getCoverPhotoById(@Parameter(description = "User Id.") @PathVariable String id)
      throws Exception {
    return ResponseEntity.ok(photoService.getPhotoByUserId(id, false));
  }
}
