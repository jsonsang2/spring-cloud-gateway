package com.wassup.springcloudgateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = RestException.class)
    public ResponseEntity<HttpErrorResponse> handleRestException(RestException ex) {
        return makeResponseEntity(ex);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<HttpErrorResponse> handleException(Exception e){
        RestException ex = new RestException(ErrorCode.InternalServerError, e.getMessage());
        return makeResponseEntity(ex);
    }

    private ResponseEntity<HttpErrorResponse> makeResponseEntity(RestException ex){

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .headers(headers)
                .body(HttpErrorResponse.makeHttpErrorResponse(ex));
    }
}
