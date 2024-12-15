/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import static com.mongodb.client.model.Filters.eq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kirjaswappi.backend.jpa.daos.PhotoDao;
import com.kirjaswappi.backend.jpa.repositories.BookRepository;
import com.kirjaswappi.backend.jpa.repositories.PhotoRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.PhotoMapper;
import com.kirjaswappi.backend.mapper.UserMapper;
import com.kirjaswappi.backend.service.entities.Photo;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BookNotFoundException;
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
  private BookRepository bookRepository;

  public Photo addProfilePhoto(String userId, MultipartFile file) throws IOException {
    return addPhoto(userId, file, "Profile photo");
  }

  public Photo addCoverPhoto(String userId, MultipartFile file) throws IOException {
    return addPhoto(userId, file, "Cover photo");
  }

  public Photo addBookPhoto(String bookId, MultipartFile file) throws IOException {
    return addPhoto(bookId, file, "Book photo");
  }

  public void deleteBookPhoto(String bookId) {
    var bookDao = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
    if (bookDao.getCoverPhoto() != null) {
      deletePhotoFromGridFs(bookDao.getCoverPhoto().getFileId());
    }
  }

  public void deleteProfilePhoto(String userId) {
    deleteUserPhoto(userId, true);
  }

  public void deleteCoverPhoto(String userId) {
    deleteUserPhoto(userId, false);
  }

  private Photo addPhoto(String userId, MultipartFile file, String title) throws IOException {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    // Delete the old photo if it exists
    var oldPhoto = "Profile photo".equals(title) ? userDao.getProfilePhoto() : userDao.getCoverPhoto();
    if (oldPhoto != null) {
      deletePhotoFromGridFs(oldPhoto.getFileId());
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
    var updatedPhotodao = photoRepository.save(photoDao);

    // Update the user with the new photo
    if ("Profile photo".equals(title)) {
      userDao.setProfilePhoto(updatedPhotodao);
    } else {
      userDao.setCoverPhoto(updatedPhotodao);
    }
    userRepository.save(userDao);

    var photo = PhotoMapper.toEntity(updatedPhotodao);
    // add the photo bytes
    photo.setFileBytes(file.getBytes());
    return photo;
  }

  public byte[] getPhotoByUserEmail(String email, boolean isProfilePhoto) {
    User user = UserMapper.toEntity(userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new));
    return getUserPhoto(isProfilePhoto, user);
  }

  public byte[] getPhotoByUserId(String userId, boolean isProfilePhoto) {
    User user = UserMapper.toEntity(userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
    return getUserPhoto(isProfilePhoto, user);
  }

  private byte[] getUserPhoto(boolean isProfilePhoto, User user) {
    Photo photo = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    checkIfPhotoExists(photo == null);
    return getPhoto(photo);
  }

  private byte[] getPhoto(Photo photo) {
    var gridFSFile = getGridFSFile(photo.getFileId());
    if (gridFSFile == null) {
      throw new ResourceNotFoundException("photoNotFound");
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    gridFSBucket.downloadToStream(gridFSFile.getObjectId(), baos);
    return baos.toByteArray();
  }

  private GridFSFile getGridFSFile(ObjectId fileId) {
    return gridFSBucket.find(eq("_id", fileId)).first();
  }

  private static void checkIfPhotoExists(boolean photo) {
    if (photo) {
      throw new ResourceNotFoundException("photoNotFound");
    }
  }

  private void deleteUserPhoto(String userId, boolean isProfilePhoto) {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var photo = isProfilePhoto ? userDao.getProfilePhoto() : userDao.getCoverPhoto();

    checkIfPhotoExists(photo == null);

    deletePhotoFromGridFs(photo.getFileId());

    // Update the user
    if (isProfilePhoto) {
      userDao.setProfilePhoto(null);
    } else {
      userDao.setCoverPhoto(null);
    }
    userRepository.save(userDao);
  }

  private void deletePhotoFromGridFs(ObjectId fileId) {
    // Delete the photo from GridFS
    gridFSBucket.delete(fileId);
  }
}