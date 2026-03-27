package com.aidevquiz.result.dto;

import java.time.Instant;

public record ResultResponse(
        Long id,
        Long userId,
        Long quizId,
        String domain,
        String topic,
        String difficulty,
        int score,
        int totalQuestions,
        Instant createdAt
) {
}
