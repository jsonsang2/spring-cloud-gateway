package com.wassup.springcloudgateway.configuration.feign;

import com.fasterxml.jackson.databind.DeserializationFeature;
import feign.Client;
import feign.Contract;
import feign.Logger;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class FeignClientConfig implements Jackson2ObjectMapperBuilderCustomizer {

    @Bean
    public Contract feignContract() {return new SpringMvcContract();}

    @Bean
    Logger.Level feignLoggerLevel() {return Logger.Level.FULL;}

    @Bean
    Logger feignLogger() {return new CustomFeignLogger();}

    @Bean
    public Client feignClient(ApacheHttpClientFactory httpClientFactory,
                              HttpClientConnectionManager httpClientConnectionManager,
                              FeignHttpClientProperties httpClientProperties){

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(httpClientProperties.getConnectionTimeout())
                .setRedirectsEnabled(httpClientProperties.isFollowRedirects())
                .build();

        HttpClientBuilder httpClientBuilder = httpClientFactory.createBuilder()
                .setConnectionManager(httpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections();

        return new ApacheHttpClient(httpClientBuilder.build());
    }

    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder
                .featuresToEnable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
