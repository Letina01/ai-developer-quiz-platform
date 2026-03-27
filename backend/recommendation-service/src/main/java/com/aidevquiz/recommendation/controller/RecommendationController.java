package com.aidevquiz.recommendation.controller;

import com.aidevquiz.recommendation.dto.RecommendationResponse;
import com.aidevquiz.recommendation.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/users/{userId}")
    public RecommendationResponse getRecommendations(@PathVariable("userId") Long userId) {
        return recommendationService.getRecommendations(userId);
    }
}
