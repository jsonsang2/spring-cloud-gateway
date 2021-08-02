package com.wassup.springcloudgateway.configuration.feign.authorization;

import com.wassup.springcloudgateway.exception.ErrorCode;
import com.wassup.springcloudgateway.exception.RestException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if(400 <= response.status() && response.status() < 500){
            return new RestException(ErrorCode.Unauthorized, "Invalid token request");
        } else if(500 <= response.status() && response.status() < 600){
            return new RestException(ErrorCode.InternalServerError);
        }
        return new RestException(ErrorCode.InternalServerError);
    }
}
