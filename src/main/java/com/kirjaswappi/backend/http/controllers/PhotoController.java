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

import java.io.IOException;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.kirjaswappi.backend.service.entities.Photo;

@RestController
@RequestMapping(API_BASE + PHOTOS)
@Validated
public class PhotoController {
  @Autowired
  private PhotoService photoService;

  @PostMapping(PROFILE_PHOTO)
  @Operation(summary = "Add a profile photo.", description = "Add a profile photo to a user.", responses = {
      @ApiResponse(responseCode = "200", description = "Profile Photo Added.") })
  public ResponseEntity<byte[]> addProfilePhoto(@Valid @ModelAttribute CreatePhotoRequest request) throws IOException {
    Photo photo = photoService.addProfilePhoto(request.getUserId(), request.getImage());
    return getPhotoResponse(photo.getFileBytes());
  }

  @PostMapping(COVER_PHOTO)
  @Operation(summary = "Add a cover photo.", description = "Add a cover photo to a user.", responses = {
      @ApiResponse(responseCode = "200", description = "Cover Photo Added.") })
  public ResponseEntity<byte[]> addCoverPhoto(@Valid @ModelAttribute CreatePhotoRequest request) throws IOException {
    Photo photo = photoService.addCoverPhoto(request.getUserId(), request.getImage());
    return getPhotoResponse(photo.getFileBytes());
  }

  @DeleteMapping(PROFILE_PHOTO + ID)
  @Operation(summary = "Delete a profile photo.", description = "Delete a profile photo of a user.", responses = {
      @ApiResponse(responseCode = "204", description = "Profile Photo Deleted.") })
  public ResponseEntity<Void> deleteProfilePhoto(@Parameter(description = "User Id.") @PathVariable String id) {
    photoService.deleteProfilePhoto(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(COVER_PHOTO + ID)
  @Operation(summary = "Delete a cover photo.", description = "Delete a cover photo of a user.", responses = {
      @ApiResponse(responseCode = "204", description = "Cover Photo Deleted.") })
  public ResponseEntity<Void> deleteCoverPhoto(@Parameter(description = "User Id.") @PathVariable String id) {
    photoService.deleteCoverPhoto(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(PROFILE_PHOTO + BY_EMAIL + EMAIL)
  @Operation(summary = "Get a profile photo by email.", description = "Get a profile photo of a user by email.", responses = {
      @ApiResponse(responseCode = "200", description = "Profile Photo Found.") })
  public ResponseEntity<byte[]> getProfilePhotoByEmail(
      @Parameter(description = "User Email.") @PathVariable String email) {
    return getPhotoResponse(photoService.getPhotoByUserEmail(email, true));
  }

  @GetMapping(COVER_PHOTO + BY_EMAIL + EMAIL)
  @Operation(summary = "Get a cover photo by email.", description = "Get a cover photo of a user by email.", responses = {
      @ApiResponse(responseCode = "200", description = "Cover Photo Found.") })
  public ResponseEntity<byte[]> getCoverPhotoByEmail(
      @Parameter(description = "User Email.") @PathVariable String email) {
    return getPhotoResponse(photoService.getPhotoByUserEmail(email, false));
  }

  @GetMapping(PROFILE_PHOTO + BY_ID + ID)
  @Operation(summary = "Get a profile photo by user id.", description = "Get a profile photo of a user by user id.", responses = {
      @ApiResponse(responseCode = "200", description = "Profile Photo Found.") })
  public ResponseEntity<byte[]> getProfilePhotoById(@Parameter(description = "User Id.") @PathVariable String id) {
    return getPhotoResponse(photoService.getPhotoByUserId(id, true));
  }

  @GetMapping(COVER_PHOTO + BY_ID + ID)
  @Operation(summary = "Get a cover photo by user id.", description = "Get a cover photo of a user by user id.", responses = {
      @ApiResponse(responseCode = "200", description = "Cover Photo Found.") })
  public ResponseEntity<byte[]> getCoverPhotoById(@Parameter(description = "User Id.") @PathVariable String id) {
    return getPhotoResponse(photoService.getPhotoByUserId(id, false));
  }

  private ResponseEntity<byte[]> getPhotoResponse(byte[] photoBytes) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=photo.jpg")
        .body(photoBytes);
  }
}
