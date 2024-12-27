/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.service.exceptions.ImageDeletionFailureException;
import com.kirjaswappi.backend.service.exceptions.ImageUploadFailureException;
import com.kirjaswappi.backend.service.exceptions.ImageUrlFetchFailureException;
import com.kirjaswappi.backend.service.exceptions.ResourceNotFoundException;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;

@Service
@Transactional
public class PhotoService {
  @Autowired
  private MinioClient minioClient;
  @Autowired
  private UserRepository userRepository;

  private final String bucketName = "kirjaswappi";

  // User photos:
  public String addProfilePhoto(String userId, MultipartFile file) throws Exception {
    var profilePhoto = uploadImage(file);
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (userDao.getProfilePhoto() != null) {
      deleteImage(userDao.getProfilePhoto());
    }
    userDao.setProfilePhoto(profilePhoto);
    return getDownloadUrl(profilePhoto);
  }

  public String addCoverPhoto(String userId, MultipartFile file) throws Exception {
    var coverPhoto = uploadImage(file);
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (userDao.getCoverPhoto() != null) {
      deleteImage(userDao.getCoverPhoto());
    }
    userDao.setCoverPhoto(coverPhoto);
    return getDownloadUrl(coverPhoto);
  }

  public void deleteProfilePhoto(String userId) throws Exception {
    deleteUserPhoto(userId, true);
  }

  public void deleteCoverPhoto(String userId) throws Exception {
    deleteUserPhoto(userId, false);
  }

  public String getPhotoByUserEmail(String email, boolean isProfilePhoto) throws Exception {
    var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    var photo = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    return getDownloadUrl(photo);
  }

  public String getPhotoByUserId(String userId, boolean isProfilePhoto) {
    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var photo = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    return getDownloadUrl(photo);
  }

  private void deleteUserPhoto(String userId, boolean isProfilePhoto) {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var photo = isProfilePhoto ? userDao.getProfilePhoto() : userDao.getCoverPhoto();
    deleteImage(photo);
    if (isProfilePhoto) {
      userDao.setProfilePhoto(null);
    } else {
      userDao.setCoverPhoto(null);
    }
    userRepository.save(userDao);
  }

  // Book cover photo:
  public String addBookCoverPhoto(MultipartFile file) {
    return uploadImage(file);
  }

  public void deleteBookCoverPhoto(String coverPhoto) {
    deleteImage(coverPhoto);
  }

  public String getBookCoverById(String coverPhoto) {
    return getDownloadUrl(coverPhoto);
  }

  // S3 bucket operations:
  public String uploadImage(MultipartFile file) {
    try {
      String objectName = file.getOriginalFilename();
      try (InputStream inputStream = file.getInputStream()) {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
      }
      return objectName;
    } catch (Exception e) {
      throw new ImageUploadFailureException("Failed to upload image.");
    }

  }

  public String getDownloadUrl(String objectName) {
    if (objectName == null) {
      throw new ResourceNotFoundException("photoNotFound");
    }
    try {
      return minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .method(Method.GET)
              .expiry(1, TimeUnit.HOURS)
              .build());
    } catch (Exception e) {
      throw new ImageUrlFetchFailureException("Failed to fetch image URL.");
    }
  }

  public void deleteImage(String objectName) {
    if (objectName == null) {
      throw new ResourceNotFoundException("photoNotFound");
    }
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .build());
    } catch (Exception e) {
      throw new ImageDeletionFailureException("Failed to delete image.");
    }
  }
}