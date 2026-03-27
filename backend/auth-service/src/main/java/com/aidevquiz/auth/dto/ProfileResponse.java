package com.aidevquiz.auth.dto;

public record ProfileResponse(
        Long id,
        String name,
        String email,
        String authProvider,
        String focusDomain,
        String targetRole,
        String experienceLevel,
        String currentSkills,
        String studyGoal,
        boolean profileCompleted
) {
}
