package com.aidevquiz.result.dto;

import java.time.Instant;
import java.util.List;

public record ResultResponse(
        Long id,
        Long userId,
        Long quizId,
        String domain,
        String topic,
        String difficulty,
        int score,
        int totalQuestions,
        Instant createdAt,
        List<ReviewItem> review
) {
    public record ReviewItem(
            String question,
            String selectedAnswer,
            String correctAnswer,
            boolean correct,
            String explanation
    ) {}
}
