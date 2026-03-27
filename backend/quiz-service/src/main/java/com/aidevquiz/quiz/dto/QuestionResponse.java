package com.aidevquiz.quiz.dto;

public record QuestionResponse(
        Long id,
        String question,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String correctAnswer,
        String explanation
) {
}
