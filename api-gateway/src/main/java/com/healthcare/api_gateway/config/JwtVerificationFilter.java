package com.healthcare.api_gateway.config;

import org.springframework.http.HttpHeaders;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpMethod;

import java.util.List;

@Component
public class JwtVerificationFilter implements GlobalFilter, Ordered {

    private static final List<PublicEndpoint> PUBLIC_ENDPOINTS = List.of(
            new PublicEndpoint(HttpMethod.POST, "/auth/login"),
            new PublicEndpoint(HttpMethod.GET, "/auth/verify"),
            new PublicEndpoint(null, "/actuator"),
            new PublicEndpoint(null, "/swagger-ui"),
            new PublicEndpoint(null, "/v3/api-docs"),
            new PublicEndpoint(HttpMethod.POST, "/api/members")
    );

    private final WebClient webClient;

    public JwtVerificationFilter(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://security-service")
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublic(request)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        return webClient.get()
                .uri("/auth/verify")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toBodilessEntity()
                .flatMap(r -> {
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header(HttpHeaders.AUTHORIZATION, authHeader)
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(e -> unauthorized(exchange));
    }

    private boolean isPublic(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        return PUBLIC_ENDPOINTS.stream().anyMatch(endpoint -> {

            boolean methodMatches =
                    endpoint.method() == null || endpoint.method().equals(method);

            boolean pathMatches =
                    path.equals(endpoint.path()) || path.startsWith(endpoint.path() + "/");

            return methodMatches && pathMatches;
        });
    }


    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

