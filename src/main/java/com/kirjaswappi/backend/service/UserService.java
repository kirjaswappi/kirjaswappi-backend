/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirjaswappi.backend.common.service.exceptions.InvalidCredentials;
import com.kirjaswappi.backend.common.utils.Util;
import com.kirjaswappi.backend.jpa.daos.UserDao;
import com.kirjaswappi.backend.jpa.repositories.UserRepository;
import com.kirjaswappi.backend.mapper.UserMapper;
import com.kirjaswappi.backend.service.entities.User;
import com.kirjaswappi.backend.service.exceptions.BadRequest;
import com.kirjaswappi.backend.service.exceptions.UserAlreadyExists;
import com.kirjaswappi.backend.service.exceptions.UserNotFound;

@Service
@Transactional
public class UserService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  UserMapper mapper;

  public User addUser(User user) {
    this.checkUserExistButNotVerified(user);
    this.checkIfUserAlreadyExists(user);

    // add salt to password:
    String salt = Util.generateSalt();
    user.setPassword(user.getPassword(), salt);

    // save user:
    UserDao dao = mapper.toDao(user, salt);
    dao.setEmailVerified(false);
    return mapper.toEntity(userRepository.save(dao));
  }

  public boolean checkIfUserExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  private void checkIfUserAlreadyExists(User user) {
    if (userRepository.findByEmailAndIsEmailVerified(user.getEmail(), true).isPresent()) {
      throw new UserAlreadyExists(user.getEmail());
    }
  }

  private void checkUserExistButNotVerified(User user) {
    // validate user exists and email is not verified:
    if (userRepository.findByEmailAndIsEmailVerified(user.getEmail(), false).isPresent()) {
      throw new BadRequest("userExistsButNotVerified", user.getEmail());
    }
  }

  public User getUser(String id) {
    return mapper.toEntity(userRepository.findById(id)
        .orElseThrow(() -> new UserNotFound(id)));
  }

  public List<User> getUsers() {
    return userRepository.findAll().stream().map(mapper::toEntity).toList();
  }

  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }

  public User updateUser(User user) {
    // validate user exists:
    var dao = userRepository.findById(user.getId())
        .orElseThrow(() -> new UserNotFound(user.getId()));

    // check email verification status:
    if (!dao.isEmailVerified()) {
      throw new BadRequest("userExistsButNotVerified", user.getEmail());
    }

    // update user details:
    updateUserDetails(user, dao);

    return mapper.toEntity(userRepository.save(dao));
  }

  private static void updateUserDetails(User user, UserDao dao) {
    dao.setFirstName(user.getFirstName());
    dao.setLastName(user.getLastName());
    dao.setStreetName(user.getStreetName());
    dao.setHouseNumber(user.getHouseNumber());
    dao.setZipCode(user.getZipCode());
    dao.setCity(user.getCity());
    dao.setCountry(user.getCountry());
    dao.setPhoneNumber(user.getPhoneNumber());
  }

  public User verifyLogin(User user) {
    // get salt from email:
    UserDao dao = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new UserNotFound(user.getEmail()));

    // check email verification status:
    if (!dao.isEmailVerified()) {
      throw new BadRequest("userExistsButNotVerified", user.getEmail());
    }

    // hash password with salt:
    String password = Util.hashPassword(user.getPassword(), dao.getSalt());

    // validate email and password and return user:
    return mapper.toEntity(userRepository.findByEmailAndPassword(dao.getEmail(), password)
        .orElseThrow(() -> new InvalidCredentials(dao.getEmail(), password)));
  }

  public void verifyCurrentPassword(User user) {
    // get salt from email:
    UserDao dao = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new UserNotFound(user.getEmail()));

    // hash password with salt:
    String password = Util.hashPassword(user.getPassword(), dao.getSalt());

    // validate email and password:
    if (userRepository.findByEmailAndPassword(dao.getEmail(), password).isEmpty()) {
      throw new BadRequest("currentPasswordMismatch", user.getPassword());
    }
  }

  public String changePassword(User user) {
    // get user from email:
    UserDao dao = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new UserNotFound(user.getEmail()));

    // forbid newPassword to be the same as currentPassword:
    String currentPassword = dao.getPassword();
    String newPassword = Util.hashPassword(user.getPassword(), dao.getSalt());
    if (currentPassword.equals(newPassword)) {
      throw new BadRequest("newPasswordCannotBeSameAsCurrentPassword", newPassword);
    }

    // add new salt to new password:
    String newSalt = Util.generateSalt();
    String newPasswordWithNewSalt = Util.hashPassword(user.getPassword(), newSalt);

    // save password:
    dao.setSalt(newSalt);
    dao.setPassword(newPasswordWithNewSalt);
    userRepository.save(dao);

    return dao.getEmail();
  }

  public String verifyEmail(String email) {
    // get user from email:
    UserDao dao = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFound(email));

    // update email verification status:
    dao.setEmailVerified(true);
    userRepository.save(dao);

    return dao.getEmail();
  }
}
