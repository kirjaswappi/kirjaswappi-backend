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
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.kirjaswappi.backend.common.exceptions.BusinessException;

@Component
public class ErrorUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorUtils.class);
  private final MessageSource messageSource;

  public ErrorUtils(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public ErrorResponse buildErrorResponse(BusinessException exception) {
    String topLevelErrorMessage = this.resolveMessage(exception.getMessageKey(), exception.getMessageParams(),
        null);
    String target = getCurrentPath();
    var topLevelError = new ErrorResponse.Error(exception.getMessageKey(), topLevelErrorMessage, target);
    if (exception.getMessageParams().length > 0) {
      topLevelError.addErrorDetail(exception.getMessageKey(), exception.getMessageParams()[0].toString(), target);
    }
    return new ErrorResponse(topLevelError);
  }

  public ErrorResponse buildErrorResponse(BusinessException exception, WebRequest webRequest) {
    String topLevelErrorMessage = this.resolveMessage(exception.getMessageKey(), exception.getMessageParams(),
        webRequest.getLocale());
    String target = getCurrentPath();
    var topLevelError = new ErrorResponse.Error(exception.getMessageKey(), topLevelErrorMessage, target);
    return new ErrorResponse(topLevelError);
  }

  public String resolveMessage(String errorCode, Object[] params, Locale locale) {
    try {
      return this.messageSource.getMessage(errorCode, params, locale);
    } catch (NoSuchMessageException e) {
      LOGGER.warn("Translatable text missing for messageKey {}", errorCode);
      return errorCode;
    }
  }

  public String getErrorTarget(Path propertyPath) {
    return StreamSupport.stream(propertyPath.spliterator(), false)
        // drop segments for method name (update, ...) and parameter name
        // (materialDemand)
        .dropWhile(node -> !node.getKind().equals(ElementKind.PROPERTY)).map(this::getErrorTarget)
        .collect(Collectors.joining("/"));
  }

  public String getErrorTarget(Path.Node node) {
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
