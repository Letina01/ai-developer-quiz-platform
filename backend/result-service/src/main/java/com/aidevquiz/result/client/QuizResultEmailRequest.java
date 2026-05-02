package com.aidevquiz.result.client;

public record QuizResultEmailRequest(
    String to,
    String userName,
    String quizTitle,
    int totalQuestions,
    int correctAnswers,
    double scorePercentage,
    String recommendations
) {}
