package com.aidevquiz.result.client;

import java.util.List;

public record QuizAnswerResponse(
        Long id,
        Long createdBy,
        String domain,
        String topic,
        String difficulty,
        List<QuizAnswerQuestion> questions
) {
}
