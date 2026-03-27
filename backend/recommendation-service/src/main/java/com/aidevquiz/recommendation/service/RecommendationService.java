package com.aidevquiz.recommendation.service;

import com.aidevquiz.recommendation.dto.RecommendationResponse;

public interface RecommendationService {
    RecommendationResponse getRecommendations(Long userId);
}
