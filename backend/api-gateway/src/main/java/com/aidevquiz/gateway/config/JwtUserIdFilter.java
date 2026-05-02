package com.aidevquiz.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
public class JwtUserIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtUserIdFilter.class);
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_NAME_HEADER = "X-User-Name";

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/ai/generate",
            "/actuator/health",
            "/swagger-ui",
            "/api-docs",
            "/v3/api-docs"
    );

    @Value("${app.public-paths:}")
    private String customPublicPaths;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(auth -> auth != null && auth.getPrincipal() instanceof Jwt)
                .map(auth -> (Jwt) auth.getPrincipal())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(jwt -> {
                    Long userId = extractUserId(jwt);
                    String email = jwt.getClaimAsString("email");
                    String name = jwt.getClaimAsString("name");

                    if (userId != null) {
                        log.debug("Extracted user ID: {} from JWT for path: {}", userId, path);
                        
                        ServerHttpRequest modifiedRequest = request.mutate()
                                .header(USER_ID_HEADER, String.valueOf(userId))
                                .header(USER_EMAIL_HEADER, email != null ? email : "")
                                .header(USER_NAME_HEADER, name != null ? name : "")
                                .build();

                        ServerWebExchange updatedExchange = exchange.mutate().request(modifiedRequest).build();
                        return chain.filter(updatedExchange);
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange))
                .onErrorResume(e -> {
                    log.warn("Error extracting user from JWT: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private Long extractUserId(Jwt jwt) {
        Object userIdClaim = jwt.getClaim("userId");
        
        if (userIdClaim instanceof Long) {
            return (Long) userIdClaim;
        } else if (userIdClaim instanceof Integer) {
            return ((Integer) userIdClaim).longValue();
        } else if (userIdClaim instanceof String) {
            try {
                return Long.parseLong((String) userIdClaim);
            } catch (NumberFormatException e) {
                log.warn("Could not parse userId from JWT: {}", userIdClaim);
            }
        }
        
        String subject = jwt.getSubject();
        if (subject != null && subject.matches("\\d+")) {
            return Long.parseLong(subject);
        }
        
        return null;
    }

    private boolean isPublicPath(String path) {
        if (path.startsWith("/api/auth/login") || 
            path.startsWith("/api/auth/register") ||
            path.startsWith("/api/auth/forgot-password") ||
            path.startsWith("/actuator") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/api-docs") ||
            path.startsWith("/v3/api-docs")) {
            return true;
        }
        
        if (customPublicPaths != null && !customPublicPaths.isEmpty()) {
            for (String publicPath : customPublicPaths.split(",")) {
                if (path.startsWith(publicPath.trim())) {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
