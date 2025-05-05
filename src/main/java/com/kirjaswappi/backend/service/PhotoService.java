/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.common.service.ImageService;
import com.kirjaswappi.backend.jpa.daos.PhotoDao;
import com.kirjaswappi.backend.jpa.repositories.PhotoRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.PhotoMapper;
import com.kirjaswappi.backend.service.entities.Photo;
import com.kirjaswappi.backend.service.exceptions.ResourceNotFoundException;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;

@Service
@Transactional
public class PhotoService {
  @Autowired
  private ImageService imageService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PhotoRepository photoRepository;

  public String addProfilePhoto(String userId, MultipartFile file) {
    return addUserPhoto(userId, file, true);
  }

  public String addCoverPhoto(String userId, MultipartFile file) {
    return addUserPhoto(userId, file, false);
  }

  public void deleteProfilePhoto(String userId) {
    deleteUserPhoto(userId, true);
  }

  public void deleteCoverPhoto(String userId) {
    deleteUserPhoto(userId, false);
  }

  public String getPhotoByUserEmail(String email, boolean isProfilePhoto) {
    var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    var uniqueId = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    return imageService.getDownloadUrl(uniqueId);
  }

  public String getPhotoByUserId(String userId, boolean isProfilePhoto) {
    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var uniqueId = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    return imageService.getDownloadUrl(uniqueId);
  }

  public void addBookCoverPhoto(MultipartFile file, String uniqueId) {
    // var uniqueId = bookId + "-" + "BookCoverPhoto";
    imageService.uploadImage(file, uniqueId);
    // return uniqueId;
  }

  public void deleteBookCoverPhoto(String uniqueId) {
    imageService.deleteImage(uniqueId);
  }

  public String getBookCoverPhoto(String uniqueId) {
    return imageService.getDownloadUrl(uniqueId);
  }

  private String addUserPhoto(String userId, MultipartFile file, boolean isProfilePhoto) {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var uniqueId = userDao.getId() + "-" + (isProfilePhoto ? "ProfilePhoto" : "CoverPhoto");
    imageService.uploadImage(file, uniqueId);
    if (isProfilePhoto) {
      userDao.setProfilePhoto(uniqueId);
    } else {
      userDao.setCoverPhoto(uniqueId);
    }
    userRepository.save(userDao);
    return imageService.getDownloadUrl(uniqueId);
  }

  private void deleteUserPhoto(String userId, boolean isProfilePhoto) {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var uniqueId = isProfilePhoto ? userDao.getProfilePhoto() : userDao.getCoverPhoto();
    imageService.deleteImage(uniqueId);
    if (isProfilePhoto) {
      userDao.setProfilePhoto(null);
    } else {
      userDao.setCoverPhoto(null);
    }
    userRepository.save(userDao);
  }

  public void deleteSupportedCoverPhoto(String coverPhotoId) {
    var dao = photoRepository.findById(coverPhotoId)
        .orElseThrow(() -> new ResourceNotFoundException("coverPhotoNotFound", coverPhotoId));
    imageService.deleteImage(dao.getCoverPhoto());
    photoRepository.delete(dao);
  }

  public String addSupportedCoverPhoto(MultipartFile file) {
    var id = UUID.randomUUID().toString();
    var uniqueId = id + "-Supported-Cover-Photo";

    imageService.uploadImage(file, uniqueId);

    var photo = new PhotoDao(id, uniqueId);
    photoRepository.save(photo);

    return imageService.getDownloadUrl(uniqueId);
  }

  public List<Photo> findSupportedCoverPhoto() {
    List<Photo> supportedPhotos = new ArrayList<>();
    for (PhotoDao supportedPhoto : photoRepository.findAll()) {
      supportedPhotos.add(PhotoMapper.toEntity(supportedPhoto.getId(),
          imageService.getDownloadUrl(supportedPhoto.getCoverPhoto())));
    }
    return supportedPhotos;
  }
}