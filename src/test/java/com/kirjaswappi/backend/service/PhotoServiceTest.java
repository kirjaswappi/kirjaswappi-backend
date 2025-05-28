/*
 * Copyright (c) 2025 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.minio.MinioClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.common.service.ImageService;
import com.kirjaswappi.backend.service.exceptions.ImageDeletionFailureException;
import com.kirjaswappi.backend.service.exceptions.ImageUploadFailureException;

class PhotoServiceTest {
  @Mock
  private MinioClient minioClient;
  @Mock
  private ImageService imageService;
  @InjectMocks
  private PhotoService photoService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Throws when image upload fails for book cover photo")
  void addBookCoverPhotoThrowsOnFailure() {
    MultipartFile file = mock(MultipartFile.class);
    doThrow(new ImageUploadFailureException()).when(imageService).uploadImage(any(MultipartFile.class), anyString());
    assertThrows(ImageUploadFailureException.class, () -> photoService.addBookCoverPhoto(file, "id"));
  }

  @Test
  @DisplayName("Adds book cover photo successfully")
  void addBookCoverPhotoSuccess() {
    MultipartFile file = mock(MultipartFile.class);
    doNothing().when(imageService).uploadImage(any(MultipartFile.class), anyString());
    assertDoesNotThrow(() -> photoService.addBookCoverPhoto(file, "id"));
  }

  @Test
  @DisplayName("Returns URL for book cover photo")
  void getBookCoverPhotoReturnsUrl() {
    String uniqueId = "cover-photo-id";
    String expectedUrl = "http://example.com/photo.jpg";
    when(imageService.getDownloadUrl(uniqueId)).thenReturn(expectedUrl);
    String actualUrl = photoService.getBookCoverPhoto(uniqueId);
    assertEquals(expectedUrl, actualUrl);
  }

  @Test
  @DisplayName("Returns null if book cover photo not found")
  void getBookCoverPhotoReturnsNullIfNotFound() {
    String uniqueId = "notfound";
    when(imageService.getDownloadUrl(uniqueId)).thenReturn(null);
    String actualUrl = photoService.getBookCoverPhoto(uniqueId);
    assertNull(actualUrl);
  }

  @Test
  @DisplayName("Deletes book cover photo via image service")
  void deleteBookCoverPhotoCallsImageService() {
    String uniqueId = "cover-photo-id";
    doNothing().when(imageService).deleteImage(uniqueId);
    photoService.deleteBookCoverPhoto(uniqueId);
    verify(imageService, times(1)).deleteImage(uniqueId);
  }

  @Test
  @DisplayName("Throws if image service fails when deleting book cover photo")
  void deleteBookCoverPhotoThrowsIfImageServiceFails() {
    String uniqueId = "cover-photo-id";
    doThrow(new ImageDeletionFailureException()).when(imageService).deleteImage(uniqueId);
    assertThrows(ImageDeletionFailureException.class, () -> photoService.deleteBookCoverPhoto(uniqueId));
  }
}
