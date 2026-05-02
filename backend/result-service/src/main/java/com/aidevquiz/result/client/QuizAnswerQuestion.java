package com.aidevquiz.result.client;

public record QuizAnswerQuestion(
        Long id,
        String question,
        String correctAnswer,
        String explanation
) {
}
