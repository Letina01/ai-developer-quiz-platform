package com.aidevquiz.recommendation.dto;

import java.util.List;

public record RecommendationResponse(
        Long userId,
        List<String> strengths,
        List<String> weakAreas,
        List<String> recommendedNextQuizzes
) {
}
