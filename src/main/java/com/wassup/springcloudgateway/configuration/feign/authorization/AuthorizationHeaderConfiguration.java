package com.wassup.springcloudgateway.configuration.feign.authorization;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
public class AuthorizationHeaderConfiguration {

    @Value("${client.id:client-id}")
    private String clientId;

    @Value("${client.secret:secret}")
    private String clientSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("x-app-id", clientId);
            requestTemplate.header(HttpHeaders.ACCEPT, MediaType.TEXT_XML_VALUE);
        };
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(clientId, clientSecret);
    }
}
