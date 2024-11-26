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
import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.jpa.daos.PhotoDao;
import com.kirjaswappi.backend.jpa.repositories.PhotoRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.UserMapper;
import com.kirjaswappi.backend.service.entities.Photo;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.ResourceNotFound;
import com.kirjaswappi.backend.service.exceptions.UserNotFound;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

@Service
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
    return addPhoto(userId, file, true);
  }

  public String addCoverPhoto(String userId, MultipartFile file) throws IOException {
    return addPhoto(userId, file, false);
  }

  private String addPhoto(String userId, MultipartFile file, boolean isProfilePhoto) throws IOException {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFound::new);

    var photoDao = new PhotoDao();
    if (isProfilePhoto) {
      photoDao.setTitle("profile-photo");
    } else {
      photoDao.setTitle("cover-photo");
    }
    photoRepository.save(photoDao);

    GridFSUploadOptions options = new GridFSUploadOptions().metadata(new Document("type", "image"));
    ObjectId fileId = gridFSBucket.uploadFromStream(photoDao.getId(), file.getInputStream(), options);
    photoDao.setFileId(fileId);
    photoRepository.save(photoDao);

    if (isProfilePhoto) {
      userDao.setProfilePhoto(photoDao);
    } else {
      userDao.setCoverPhoto(photoDao);
    }
    userRepository.save(userDao);

    return fileId.toString();
  }

  public GridFSFile getPhotoByUserEmail(String email, boolean isProfilePhoto) {
    User user = userMapper.toEntity(userRepository.findByEmail(email).orElseThrow(UserNotFound::new));
    return getGridFSFile(isProfilePhoto, user);
  }

  public GridFSFile getPhotoByUserId(String userId, boolean isProfilePhoto) {
    User user = userMapper.toEntity(userRepository.findById(userId).orElseThrow(UserNotFound::new));
    return getGridFSFile(isProfilePhoto, user);
  }

  private GridFSFile getGridFSFile(boolean isProfilePhoto, User user) {
    Photo photo = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    if (photo == null) {
      throw new ResourceNotFound("photoNotFound");
    }
    return gridFSBucket.find(eq("_id", photo.getFileId())).first();
  }
}