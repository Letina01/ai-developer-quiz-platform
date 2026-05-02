package com.aidevquiz.email.dto;

public record QuizResultEmailRequest(
    String to,
    String userName,
    String quizTitle,
    int totalQuestions,
    int correctAnswers,
    double scorePercentage,
    String recommendations
) {}
