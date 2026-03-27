package com.aidevquiz.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank String name,
        @NotBlank String focusDomain,
        @NotBlank String targetRole,
        @NotBlank String experienceLevel,
        @NotBlank @Size(min = 10, max = 1000) String currentSkills,
        @NotBlank @Size(min = 10, max = 1000) String studyGoal
) {
}
