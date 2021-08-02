package com.wassup.springcloudgateway.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.netty.util.internal.StringUtil;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpErrorResponse {

    private ErrorResponse error;

    public static HttpErrorResponse makeHttpErrorResponse(RestException ex){
        return HttpErrorResponse.builder()
                .error(new ErrorResponse(ex.getErrorCode().getCode(), ex.getMessage()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {
        private String code;
        private String message;
    }
}
