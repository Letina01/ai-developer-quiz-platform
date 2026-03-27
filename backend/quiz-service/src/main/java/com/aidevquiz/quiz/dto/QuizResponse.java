package com.aidevquiz.quiz.dto;

import java.time.Instant;
import java.util.List;

public record QuizResponse(
        Long id,
        Long createdBy,
        String domain,
        String topic,
        String difficulty,
        Instant createdAt,
        List<QuestionResponse> questions
) {
}
