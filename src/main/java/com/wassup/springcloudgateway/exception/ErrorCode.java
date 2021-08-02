package com.wassup.springcloudgateway.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ErrorCode {

    Unauthorized(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    InternalServerError(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError");

    private HttpStatus status;
    private String code;

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
