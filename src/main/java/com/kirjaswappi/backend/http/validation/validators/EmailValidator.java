/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.http.validation.validators;

import static com.kirjaswappi.backend.http.validation.ValidationUtil.validateEmail;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.kirjaswappi.backend.http.validation.annotations.EmailValidation;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return validateEmail(value);
  }
}
