package com.aidevquiz.result.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateResultRequest(
        @NotNull Long quizId,
        @NotNull @Size(min = 1) List<@NotBlank String> selectedAnswers,
        @Email String userEmail,
        String userName,
        String recommendations
) {
}
