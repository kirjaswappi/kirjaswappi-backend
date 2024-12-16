/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.controllers;

import static com.kirjaswappi.backend.common.utils.Constants.API_BASE;
import static com.kirjaswappi.backend.common.utils.Constants.BY_EMAIL;
import static com.kirjaswappi.backend.common.utils.Constants.BY_ID;
import static com.kirjaswappi.backend.common.utils.Constants.COVER_PHOTO;
import static com.kirjaswappi.backend.common.utils.Constants.PHOTOS;
import static com.kirjaswappi.backend.common.utils.Constants.PROFILE_PHOTO;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kirjaswappi.backend.http.dtos.requests.CreatePhotoRequest;
import com.kirjaswappi.backend.service.PhotoService;
import com.kirjaswappi.backend.service.entities.Photo;

@RestController
@RequestMapping(API_BASE + PHOTOS)
public class PhotoController {
  @Autowired
  private PhotoService photoService;

  @PostMapping(PROFILE_PHOTO)
  public ResponseEntity<byte[]> addProfilePhoto(@ModelAttribute CreatePhotoRequest request) throws IOException {
    Photo photo = photoService.addProfilePhoto(request.getUserId(), request.getImage());
    return getPhotoResponse(photo.getFileBytes());
  }

  @PostMapping(COVER_PHOTO)
  public ResponseEntity<byte[]> addCoverPhoto(@ModelAttribute CreatePhotoRequest request) throws IOException {
    Photo photo = photoService.addCoverPhoto(request.getUserId(), request.getImage());
    return getPhotoResponse(photo.getFileBytes());
  }

  @DeleteMapping(PROFILE_PHOTO)
  public ResponseEntity<Void> deleteProfilePhoto(@RequestParam("userId") String userId) {
    photoService.deleteProfilePhoto(userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(COVER_PHOTO)
  public ResponseEntity<Void> deleteCoverPhoto(@RequestParam("userId") String userId) {
    photoService.deleteCoverPhoto(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(PROFILE_PHOTO + BY_EMAIL)
  public ResponseEntity<byte[]> getProfilePhotoByEmail(@RequestParam("email") String email) {
    return getPhotoResponse(photoService.getPhotoByUserEmail(email, true));
  }

  @GetMapping(COVER_PHOTO + BY_EMAIL)
  public ResponseEntity<byte[]> getCoverPhotoByEmail(@RequestParam("email") String email) {
    return getPhotoResponse(photoService.getPhotoByUserEmail(email, false));
  }

  @GetMapping(PROFILE_PHOTO + BY_ID)
  public ResponseEntity<byte[]> getProfilePhotoById(@RequestParam("userId") String userId) {
    return getPhotoResponse(photoService.getPhotoByUserId(userId, true));
  }

  @GetMapping(COVER_PHOTO + BY_ID)
  public ResponseEntity<byte[]> getCoverPhotoById(@RequestParam("userId") String userId) {
    return getPhotoResponse(photoService.getPhotoByUserId(userId, false));
  }

  private ResponseEntity<byte[]> getPhotoResponse(byte[] photoBytes) {
    return ResponseEntity.ok()
        .contentType(MediaType.IMAGE_JPEG)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=photo.jpg")
        .body(photoBytes);
  }
}
