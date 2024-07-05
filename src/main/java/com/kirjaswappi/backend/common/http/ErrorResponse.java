/*
 * Copyright (c) 2024 KirjaSwappi or KirjaSwappi affiliate company. All rights reserved.
 * Author: Mahiuddin Al Kamal <mahiuddinalkamal>
 */
package com.kirjaswappi.backend.common.http;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * REST specific POJO for how errors should be represented in response to a REST
 * request.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(Error error) {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Error {
    private final String code;
    private final String message;
    private final String target;
    private List<ErrorDetail> details;

    public Error(String code, String message) {
      this(code, message, null);
    }

    public Error(String code, String message, String target) {
      this.code = code;
      this.message = message;
      this.target = target;
    }

    public String getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }

    public String getTarget() {
      return target;
    }

    public List<ErrorDetail> getDetails() {
      return details;
    }

    public void addErrorDetail(String code, String message, String target) {
      if (Objects.isNull(this.details)) {
        this.details = new LinkedList<>();
      }
      details.add(new ErrorDetail(code, message, target));
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private record ErrorDetail(
      String code,
      String message,
      String target
  ) {
  }
}
