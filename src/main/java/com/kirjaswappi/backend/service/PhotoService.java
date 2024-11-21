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
import com.kirjaswappi.backend.mapper.PhotoMapper;
import com.kirjaswappi.backend.mapper.UserMapper;
import com.kirjaswappi.backend.service.entities.Photo;
import com.kirjaswappi.backend.service.entities.User;
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

  @Autowired
  private PhotoMapper photoMapper;

  public String addProfilePhoto(String userId, MultipartFile file) throws IOException {
    return addPhoto(userId, file, true);
  }

  public String addCoverPhoto(String userId, MultipartFile file) throws IOException {
    return addPhoto(userId, file, false);
  }

  private String addPhoto(String userId, MultipartFile file, boolean isProfilePhoto) throws IOException {
    User user = userMapper.toEntity(userRepository.findById(userId).orElseThrow(UserNotFound::new));

    var photoDao = new PhotoDao();
    photoRepository.save(photoDao);

    GridFSUploadOptions options = new GridFSUploadOptions().metadata(new Document("type", "image"));
    ObjectId fileId = gridFSBucket.uploadFromStream(photoDao.getId(), file.getInputStream(), options);

    var entity = photoMapper.toEntity(photoDao);
    if (isProfilePhoto) {
      user.setProfilePhoto(entity);
    } else {
      user.setCoverPhoto(entity);
    }
    userRepository.save(userMapper.toDao(user));

    return fileId.toString();
  }

  public GridFSFile getPhotoByUserEmail(String email, boolean isProfilePhoto) {
    User user = userMapper.toEntity(userRepository.findByEmail(email).orElseThrow(UserNotFound::new));
    Photo photo = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    return gridFSBucket.find(eq("_id", new ObjectId(photo.getId()))).first();
  }

  public GridFSFile getPhotoByUserId(String userId, boolean isProfilePhoto) {
    User user = userMapper.toEntity(userRepository.findById(userId).orElseThrow(UserNotFound::new));
    Photo photo = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    return gridFSBucket.find(eq("_id", new ObjectId(photo.getId()))).first();
  }
}