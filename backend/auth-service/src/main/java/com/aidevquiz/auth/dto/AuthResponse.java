package com.aidevquiz.auth.dto;

public record AuthResponse(
        Long userId,
        String name,
        String email,
        boolean profileCompleted,
        String focusDomain,
        String targetRole,
        String accessToken
) {
}
