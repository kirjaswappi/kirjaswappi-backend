/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class Photo {
  private String id;
  private String title;
  private ObjectId fileId;
  @Nullable
  private MultipartFile file;
  @Nullable
  private byte[] fileBytes;

  public Photo(String id, String title, ObjectId fileId) {
    this.id = id;
    this.title = title;
    this.fileId = fileId;
  }
}
