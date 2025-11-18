package com.healthcare.api_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // admin, claims_write, member_read <-- roles en keycloak
    /*
        Testear error:
        Bearer error="insufficient_scope", 
        error_description="The request requires higher privileges than provided by the access token.", 
        error_uri="https://tools.ietf.org/html/rfc6750#section-3.1"
    */

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/members/**").hasAnyRole("MEMBER_READ", "ADMIN")
                        .pathMatchers("/api/claims/**").hasAnyRole("CLAIMS_WRITE", "ADMIN")
                        .pathMatchers("/api/documents/**").hasAnyRole("ADMIN")
                        .pathMatchers("/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }

    public static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 1. Roles de realm_access
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            authorities.addAll(
                    realmRoles.stream()
                            .map(role -> "ROLE_" + role.toUpperCase())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
            );
        }

        // 2. Roles de resource_access.api-gateway
        Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().get("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("api-gateway")) {
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("api-gateway");

            if (clientAccess.containsKey("roles")) {
                List<String> clientRoles = (List<String>) clientAccess.get("roles");

                authorities.addAll(
                        clientRoles.stream()
                                .map(role -> "ROLE_" + role.toUpperCase())
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                );
            }
        }

        return authorities;
    }
}

}