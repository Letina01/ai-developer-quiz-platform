package com.aidevquiz.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserIdFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(UserIdFilter.class);

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/auth/validate-reset-token",
            "/api/auth/reset-password",
            "/oauth2/",
            "/login/oauth2/",
            "/actuator/",
            "/api/email/",
            "/swagger-ui",
            "/v3/api-docs"
    );

    private final ReactiveJwtDecoder jwtDecoder;

    public UserIdFilter(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        
        if (isPublicPath(path)) {
            log.debug("Skipping JWT extraction for public path: {}", path);
            return chain.filter(exchange);
        }
        
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        
        String token = authHeader.substring(7);
        return jwtDecoder.decode(token)
                .flatMap(jwt -> {
                    Long userId = extractUserId(jwt.getClaim("userId"), jwt.getSubject());
                    if (userId != null) {
                        final String userIdStr = userId.toString();
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .headers(headers -> {
                                    headers.remove("X-User-Id");
                                    headers.set("X-User-Id", userIdStr);
                                })
                                .build();
                        log.debug("Extracted userId: {} for path: {}", userId, path);
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    log.debug("JWT decode failed for path {}: {}", path, e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private Long extractUserId(Object userIdClaim, String subject) {
        if (userIdClaim instanceof Long userId) {
            return userId;
        }
        if (userIdClaim instanceof Integer userId) {
            return userId.longValue();
        }
        if (userIdClaim instanceof String userId) {
            try {
                return Long.parseLong(userId);
            } catch (NumberFormatException ignored) {
                // fallback to subject parsing below
            }
        }
        if (subject != null) {
            try {
                return Long.parseLong(subject);
            } catch (NumberFormatException ignored) {
                // no usable subject
            }
        }
        return null;
    }
    
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::contains);
    }
}
