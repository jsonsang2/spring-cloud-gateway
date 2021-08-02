package com.wassup.springcloudgateway.service;

import com.wassup.springcloudgateway.model.feign.AuthorizeToken;
import com.wassup.springcloudgateway.service.feign.AuthorizationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final AuthorizationClient authorizationClient;

    public AuthorizeToken authorizeToken(String token) {
        return authorizationClient.authorizeToken(token);
    }
}
