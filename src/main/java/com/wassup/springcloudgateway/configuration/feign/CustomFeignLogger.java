package com.wassup.springcloudgateway.configuration.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import org.json.XML;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;

public class CustomFeignLogger extends Logger {

    private static final org.slf4j.Logger httpLogger = LoggerFactory.getLogger("HTTP_LOGGER");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        int bodyLength = 0;
        if (request.body() != null) {
            bodyLength = request.body().length;
        }
        String httpMethod = request.httpMethod().name();
        String url = request.url();
        httpLogger.debug("---> {} {}, ({}-byte body)", httpMethod, url, bodyLength);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        if (logLevel.ordinal() > Level.NONE.ordinal()) {
            Map<String, Object> logMap = getCommonLogMap();
            logMap.put("request", getRequestPart(configKey, logLevel, response));

            byte[] bodyData = getBodyData(logLevel, response);
            logMap.put("response", getResponsePart(response, bodyData));

            String log = objectMapper.writeValueAsString(logMap);
            httpLogger.info(log);

            if (bodyData.length > 0) {
                return response.toBuilder().body(bodyData).build();
            }
        }
        return response;
    }

    private byte[] getBodyData(Level logLevel, Response response) throws IOException {
        if (response.body() != null && !(response.status() == 204 || response.status() == 205) && logLevel.ordinal() >= Level.FULL.ordinal()) {
            return Util.toByteArray(response.body().asInputStream());
        }
        return EMPTY_BYTE_ARRAY;
    }

    private Map<String, Object> getCommonLogMap() {
        Map<String, Object> logMap = new LinkedHashMap<>();
        String timestamp = LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss.SSS").withZone(ZoneOffset.UTC));
        logMap.put("timestamp", timestamp);
        return logMap;
    }

    private Map<String, Object> getResponsePart(Response response, byte[] bodyData) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("status", response.status());
        responseMap.put("reason", response.reason());
        if (bodyData.length > 0) {
            String decodeBody = decodeOrDefault(bodyData, UTF_8, "Binary data");
            if (StringUtils.startsWithIgnoreCase(decodeBody, "<?xml")) {
                responseMap.put("body", xmlToJsonString(decodeBody));
            } else {
                responseMap.put("body", decodeBody);
            }
        }
        return responseMap;
    }

    private Object xmlToJsonString(String xml) {
        try {
            return objectMapper.readValue(XML.toJSONObject(xml).toString(), Object.class);
        } catch (Exception e) {
            return xml;
        }
    }

    private Map<String, Object> getRequestPart(String configKey, Level logLevel, Response response) {
        Map<String, Object> requestMap = new LinkedHashMap<>();
        requestMap.put("method", response.request().httpMethod());
        requestMap.put("url", response.request().url());

        if (logLevel.ordinal() >= Level.FULL.ordinal()) {
            requestMap.put("header", flattenRequestHeader(response.request().headers()));
            if (response.request().httpMethod().name().equals(HttpMethod.POST.name())) {
                requestMap.put("body", response.request().body());
            }
        }
        return requestMap;
    }

    private Map<String, String> flattenRequestHeader(Map<String, Collection<String>> headers) {
        return null;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        String log = format(configKey, format, args);
        httpLogger.info(log);
    }

    protected String format(String configKey, String format, Object... args) {
        return String.format(methodTag(configKey).concat(format), args);
    }
}
