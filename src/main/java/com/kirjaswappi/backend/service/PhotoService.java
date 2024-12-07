/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.jpa.daos.PhotoDao;
import com.kirjaswappi.backend.jpa.repositories.PhotoRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.UserMapper;
import com.kirjaswappi.backend.service.entities.Photo;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.ResourceNotFoundException;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

@Service
@Transactional
public class PhotoService {
  @Autowired
  private GridFSBucket gridFSBucket;

  @Autowired
  private PhotoRepository photoRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserMapper userMapper;

  public String addProfilePhoto(String userId, MultipartFile file) throws IOException {
    return addPhoto(userId, file, "profile-photo");
  }

  public String addCoverPhoto(String userId, MultipartFile file) throws IOException {
    return addPhoto(userId, file, "cover-photo");
  }

  public void deleteProfilePhoto(String userId) {
    deletePhoto(userId, true);
  }

  public void deleteCoverPhoto(String userId) {
    deletePhoto(userId, false);
  }

  private String addPhoto(String userId, MultipartFile file, String title) throws IOException {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    // Delete the old photo if it exists
    var oldPhoto = "profile-photo".equals(title) ? userDao.getProfilePhoto() : userDao.getCoverPhoto();
    if (oldPhoto != null) {
      gridFSBucket.delete(oldPhoto.getFileId());
    }

    // Save the new photo
    var photoDao = new PhotoDao();
    photoDao.setTitle(title);
    photoRepository.save(photoDao);

    // Save the photo to GridFS
    GridFSUploadOptions options = new GridFSUploadOptions().metadata(new Document("type", "image"));
    ObjectId fileId = gridFSBucket.uploadFromStream(photoDao.getId(), file.getInputStream(), options);

    // Update the photoDao with the fileId
    photoDao.setFileId(fileId);
    photoRepository.save(photoDao);

    // Update the user with the new photo
    if ("profile-photo".equals(title)) {
      userDao.setProfilePhoto(photoDao);
    } else {
      userDao.setCoverPhoto(photoDao);
    }
    userRepository.save(userDao);

    return fileId.toString();
  }

  public GridFSFile getPhotoByUserEmail(String email, boolean isProfilePhoto) {
    User user = userMapper.toEntity(userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new));
    return getGridFSFile(isProfilePhoto, user);
  }

  public GridFSFile getPhotoByUserId(String userId, boolean isProfilePhoto) {
    User user = userMapper.toEntity(userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
    return getGridFSFile(isProfilePhoto, user);
  }

  private GridFSFile getGridFSFile(boolean isProfilePhoto, User user) {
    Photo photo = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    checkIfPhotoExists(photo == null);
    return gridFSBucket.find(eq("_id", photo.getFileId())).first();
  }

  private static void checkIfPhotoExists(boolean photo) {
    if (photo) {
      throw new ResourceNotFoundException("photoNotFound");
    }
  }

  private void deletePhoto(String userId, boolean isProfilePhoto) {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var photo = isProfilePhoto ? userDao.getProfilePhoto() : userDao.getCoverPhoto();

    checkIfPhotoExists(photo == null);

    // Delete the photo from GridFS
    gridFSBucket.delete(photo.getFileId());

    // Update the user
    if (isProfilePhoto) {
      userDao.setProfilePhoto(null);
    } else {
      userDao.setCoverPhoto(null);
    }
    userRepository.save(userDao);
  }
}