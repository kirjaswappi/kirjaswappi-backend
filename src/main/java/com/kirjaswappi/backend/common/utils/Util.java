/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.utils;

import java.io.IOException;
import java.io.OutputStream;

import lombok.NoArgsConstructor;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class Util {
  public static String generateSalt() {
    return BCrypt.gensalt();
  }

  public static String hashPassword(String password, String salt) {
    return BCrypt.hashpw(password, salt);
  }

  public static MultipartFile convertByteArrayToMultipartFile(byte[] byteArray, String fileName, String contentType)
      throws IOException {
    if (byteArray == null || byteArray.length == 0) {
      return null;
    }

    FileItem fileItem = new DiskFileItem("file", contentType, true, fileName, byteArray.length, null);
    try (OutputStream outputStream = fileItem.getOutputStream()) {
      outputStream.write(byteArray);
    }
    return new FileItemMultipartFile(fileItem);
  }
}
