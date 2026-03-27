package com.aidevquiz.result.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResultRequest(
        @NotNull Long userId,
        @NotNull Long quizId,
        @NotBlank String domain,
        @NotBlank String topic,
        @NotBlank String difficulty,
        @Min(0) int score,
        @Min(1) int totalQuestions
) {
}
