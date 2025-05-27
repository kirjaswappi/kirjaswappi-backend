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
  @DisplayName("Should throw ImageUploadFailureException when upload fails")
  void addBookCoverPhotoThrowsOnFailure() {
    MultipartFile file = mock(MultipartFile.class);
    doThrow(new ImageUploadFailureException()).when(imageService).uploadImage(any(MultipartFile.class), anyString());
    assertThrows(ImageUploadFailureException.class, () -> photoService.addBookCoverPhoto(file, "id"));
  }

  @Test
  @DisplayName("Should return image URL when getBookCoverPhoto is called")
  void getBookCoverPhotoReturnsUrl() {
    String uniqueId = "cover-photo-id";
    String expectedUrl = "http://example.com/photo.jpg";
    when(imageService.getDownloadUrl(uniqueId)).thenReturn(expectedUrl);
    String actualUrl = photoService.getBookCoverPhoto(uniqueId);
    assertEquals(expectedUrl, actualUrl);
  }

  @Test
  @DisplayName("Should call imageService.deleteImage when deleteBookCoverPhoto is called")
  void deleteBookCoverPhotoCallsImageService() {
    String uniqueId = "cover-photo-id";
    doNothing().when(imageService).deleteImage(uniqueId);
    photoService.deleteBookCoverPhoto(uniqueId);
    verify(imageService, times(1)).deleteImage(uniqueId);
  }

  // Add more tests for getBookCoverPhoto, deleteBookCoverPhoto, etc.
}
