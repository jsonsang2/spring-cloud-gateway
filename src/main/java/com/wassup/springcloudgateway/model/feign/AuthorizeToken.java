package com.wassup.springcloudgateway.model.feign;

import lombok.Value;

@Value
public class AuthorizeToken {

    String appId;
    String userId;
    Long expiresIn;
}
