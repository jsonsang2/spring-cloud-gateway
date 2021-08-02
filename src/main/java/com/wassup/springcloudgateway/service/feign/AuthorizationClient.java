package com.wassup.springcloudgateway.service.feign;

import com.wassup.springcloudgateway.configuration.feign.authorization.AuthorizationErrorDecoder;
import com.wassup.springcloudgateway.configuration.feign.authorization.AuthorizationHeaderConfiguration;
import com.wassup.springcloudgateway.model.feign.AuthorizeToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "Authorization", url = "${license.authorization.endpoint}", primary = false,
        configuration = {
                AuthorizationErrorDecoder.class,
                AuthorizationHeaderConfiguration.class
        })
public interface AuthorizationClient {

    @GetMapping("/introspect")
    AuthorizeToken authorizeToken(@RequestParam(value = "token") String token);
}
