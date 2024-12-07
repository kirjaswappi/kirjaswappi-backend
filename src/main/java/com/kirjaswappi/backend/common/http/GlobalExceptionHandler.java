/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http;

import static com.kirjaswappi.backend.common.utils.PathProvider.getCurrentPath;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.validation.ElementKind;
import jakarta.validation.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.kirjaswappi.backend.common.exceptions.BusinessException;
import com.kirjaswappi.backend.common.exceptions.InvalidJwtTokenException;
import com.kirjaswappi.backend.common.exceptions.SystemException;
import com.kirjaswappi.backend.common.service.exceptions.InvalidCredentials;
import com.kirjaswappi.backend.service.exceptions.ResourceNotFoundException;
import com.kirjaswappi.backend.service.exceptions.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String INTERNAL_ERROR = "internalServerError";
  private static final String PATH_NOT_FOUND = "pathNotFound";
  private final MessageSource messageSource;

  @Autowired
  public GlobalExceptionHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(InvalidCredentials.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorResponse handleInvalidCredentialsException(InvalidCredentials exception, WebRequest webRequest) {
    return this.buildErrorResponse(exception, webRequest);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {
    return this.buildErrorResponse(exception, webRequest);
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleUserNotFoundException(UserNotFoundException exception, WebRequest webRequest) {
    return this.buildErrorResponse(exception, webRequest);
  }

  @ExceptionHandler(SystemException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleGenericSystemException(SystemException exception, WebRequest webRequest) {
    return new ErrorResponse(new ErrorResponse.Error(exception.getCode(),
        this.resolveMessage(INTERNAL_ERROR, null, webRequest.getLocale())));
  }

  @ExceptionHandler(InvalidJwtTokenException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorResponse handleInvalidJwtTokenException(InvalidJwtTokenException exception, WebRequest webRequest) {
    return new ErrorResponse(new ErrorResponse.Error(exception.getCode(),
        this.resolveMessage(exception.getCode(), null, webRequest.getLocale()), getCurrentPath()));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNoHandlerFoundException(WebRequest webRequest) {
    return new ErrorResponse(new ErrorResponse.Error(PATH_NOT_FOUND,
        this.resolveMessage(PATH_NOT_FOUND, null, webRequest.getLocale()), getCurrentPath()));
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleGenericBusinessException(BusinessException exception, WebRequest webRequest) {
    return this.buildErrorResponse(exception, webRequest);
  }

  private ErrorResponse buildErrorResponse(BusinessException exception, WebRequest webRequest) {
    String topLevelErrorMessage = this.resolveMessage(exception.getMessageKey(), exception.getMessageParams(),
        webRequest.getLocale());
    String target = getCurrentPath();
    var topLevelError = new ErrorResponse.Error(exception.getMessageKey(), topLevelErrorMessage, target);
    return new ErrorResponse(topLevelError);
  }

  private String resolveMessage(String errorCode, Object[] params, Locale locale) {
    try {
      return this.messageSource.getMessage(errorCode, params, locale);
    } catch (NoSuchMessageException e) {
      LOGGER.warn("Translatable text missing for messageKey {}", errorCode);
      return errorCode;
    }
  }

  private String getErrorTarget(Path propertyPath) {
    return StreamSupport.stream(propertyPath.spliterator(), false)
        // drop segments for method name (update, ...) and parameter name
        // (materialDemand)
        .dropWhile(node -> !node.getKind().equals(ElementKind.PROPERTY)).map(this::getErrorTarget)
        .collect(Collectors.joining("/"));
  }

  private String getErrorTarget(Path.Node node) {
    String nodeNameSuffix = node.getName().isBlank() ? "" : ("/" + node.getName());
    if (node.getIndex() != null) {
      return node.getIndex() + nodeNameSuffix;
    }
    if (node.getKey() != null) {
      return node.getKey() + nodeNameSuffix;
    }
    return node.getName();
  }
}
