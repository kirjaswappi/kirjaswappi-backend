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
import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.jpa.repositories.BookRepository;
import com.kirjaswappi.backend.jpa.repositories.PhotoRepository;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.PhotoMapper;
import com.kirjaswappi.backend.service.entities.Photo;
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
    return addUserPhoto(userId, file, "Profile photo");
  }

  public Photo addCoverPhoto(String userId, MultipartFile file) throws IOException {
    return addUserPhoto(userId, file, "Cover photo");
  }

  public Photo addBookCoverPhoto(MultipartFile file) throws IOException {
    return savePhoto(file, "Book photo");
  }

  public void deleteBookCoverPhoto(PhotoDao photo) {
    deletePhoto(photo);
  }

  public void deleteProfilePhoto(String userId) {
    deleteUserPhoto(userId, true);
  }

  public void deleteCoverPhoto(String userId) {
    deleteUserPhoto(userId, false);
  }

  public byte[] getPhotoByUserEmail(String email, boolean isProfilePhoto) {
    var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    return getUserPhoto(isProfilePhoto, user);
  }

  public byte[] getPhotoByUserId(String userId, boolean isProfilePhoto) {
    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    return getUserPhoto(isProfilePhoto, user);
  }

  public byte[] getBookCoverById(String bookId) {
    var bookDao = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
    var photo = bookDao.getCoverPhoto();
    if (photo == null) {
      throw new ResourceNotFoundException("photoNotFound");
    }
    return getPhoto(photo);
  }

  private Photo addUserPhoto(String userId, MultipartFile file, String title) throws IOException {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    deleteOldUserPhotoIfAny(title, userDao);
    var photo = savePhoto(file, title);
    updateUserWithNewPhoto(title, photo, userDao);
    return photo;
  }

  private void deleteOldUserPhotoIfAny(String title, UserDao userDao) {
    var oldPhoto = "Profile photo".equals(title) ? userDao.getProfilePhoto() : userDao.getCoverPhoto();
    if (oldPhoto != null) {
      deletePhoto(oldPhoto);
    }
  }

  private void updateUserWithNewPhoto(String title, Photo photo, UserDao userDao) {
    var photoDao = PhotoMapper.toDao(photo);
    if ("Profile photo".equals(title)) {
      userDao.setProfilePhoto(photoDao);
    } else {
      userDao.setCoverPhoto(photoDao);
    }
    userRepository.save(userDao);
  }

  private Photo savePhoto(MultipartFile file, String title) throws IOException {
    var photoDao = new PhotoDao();
    photoDao.setTitle(title);
    photoRepository.save(photoDao);

    GridFSUploadOptions options = new GridFSUploadOptions().metadata(new Document("type", "image"));
    ObjectId fileId = gridFSBucket.uploadFromStream(photoDao.getId(), file.getInputStream(), options);

    photoDao.setFileId(fileId);
    var updatedPhotoDao = photoRepository.save(photoDao);

    var photo = PhotoMapper.toEntity(updatedPhotoDao);
    photo.setFileBytes(file.getBytes());
    return photo;
  }

  private byte[] getUserPhoto(boolean isProfilePhoto, UserDao user) {
    var photo = isProfilePhoto ? user.getProfilePhoto() : user.getCoverPhoto();
    if (photo == null) {
      throw new ResourceNotFoundException("photoNotFound");
    }
    return getPhoto(photo);
  }

  private byte[] getPhoto(PhotoDao photo) {
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

  private void deleteUserPhoto(String userId, boolean isProfilePhoto) {
    var userDao = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var photo = isProfilePhoto ? userDao.getProfilePhoto() : userDao.getCoverPhoto();
    if (photo == null) {
      throw new ResourceNotFoundException("photoNotFound");
    }
    deletePhoto(photo);
    if (isProfilePhoto) {
      userDao.setProfilePhoto(null);
    } else {
      userDao.setCoverPhoto(null);
    }
    userRepository.save(userDao);
  }

  private void deletePhoto(PhotoDao photo) {
    deletePhotoFromGridFs(photo.getFileId());
    photoRepository.deleteById(photo.getId());
  }

  private void deletePhotoFromGridFs(ObjectId fileId) {
    gridFSBucket.delete(fileId);
  }
}