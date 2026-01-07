package com.healthcare.api_gateway.config;

import org.springframework.http.HttpMethod;

public record PublicEndpoint(HttpMethod method, String path) {
}
