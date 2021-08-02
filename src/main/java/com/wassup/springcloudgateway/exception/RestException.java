package com.wassup.springcloudgateway.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RestException extends RuntimeException{

    private ErrorCode errorCode;

    public RestException(ErrorCode errorCode){
        super();
        this.errorCode = errorCode;
    }

    public RestException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }
}
