package com.aidevquiz.recommendation.dto;

import java.time.Instant;

public record ResultSummary(Long id, Long userId, Long quizId, String topic, int score, int totalQuestions, Instant createdAt) {
}
