package com.aidevquiz.quiz.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GenerateQuizRequest(
        @NotBlank String domain,
        @NotBlank String topic,
        @NotBlank String difficulty,
        @Min(1) @Max(20) int numberOfQuestions
) {
}
