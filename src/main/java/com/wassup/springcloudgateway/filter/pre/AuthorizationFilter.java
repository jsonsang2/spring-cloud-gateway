package com.wassup.springcloudgateway.filter.pre;

import com.wassup.springcloudgateway.exception.RestException;
import com.wassup.springcloudgateway.model.feign.AuthorizeToken;
import com.wassup.springcloudgateway.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    private static final String BEARER = "Bearer";

    private static final String X_AUTHORIZED_USER_ID = "x-authorized-user-id";

    private static final String X_AUTHORIZED_APP_ID = "x-authorized-app-id";

    private final AuthorizationService authorizationService;

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Request Header 에 token 이 존재하지 않을 때
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return handleUnAuthorized(exchange); // 401 Error
            }

            // Request Header 에서 token 문자열 받아오기
            List<String> headerList = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            String authorization = Objects.requireNonNull(headerList).get(0);

            // 토큰 검증
            if (!StringUtils.hasText(authorization) || !authorization.contains(BEARER)) {
                return handleUnAuthorized(exchange); // 토큰이 일치하지 않을 때
            }

            String token = authorization.replace(BEARER, "");
            try {
                AuthorizeToken authorizeToken = authorizationService.authorizeToken(token);
                request = request.mutate()
                        .header(X_AUTHORIZED_APP_ID, authorizeToken.getAppId())
                        .header(X_AUTHORIZED_USER_ID, authorizeToken.getUserId())
                        .build();
                return chain.filter(exchange.mutate().request(request).build()); // 토큰이 일치할 때
            } catch (RestException e) {
                return handleUnAuthorized(exchange); // 토큰이 일치하지 않을 때
            }
        });
    }

    private Mono<Void> handleUnAuthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public static class Config {

    }
}
