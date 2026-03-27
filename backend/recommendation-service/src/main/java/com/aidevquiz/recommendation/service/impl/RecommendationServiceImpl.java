package com.aidevquiz.recommendation.service.impl;

import com.aidevquiz.recommendation.client.ResultClient;
import com.aidevquiz.recommendation.dto.RecommendationResponse;
import com.aidevquiz.recommendation.dto.ResultSummary;
import com.aidevquiz.recommendation.service.RecommendationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final ResultClient resultClient;

    public RecommendationServiceImpl(ResultClient resultClient) {
        this.resultClient = resultClient;
    }

    @Override
    public RecommendationResponse getRecommendations(Long userId) {
        List<ResultSummary> results = resultClient.getResults(userId);
        if (results.isEmpty()) {
            return new RecommendationResponse(userId, List.of("Start taking quizzes!"), List.of("Not enough data"), List.of("General Knowledge Quiz"));
        }

        Map<String, List<ResultSummary>> resultsByTopic = results.stream()
                .collect(Collectors.groupingBy(ResultSummary::topic));

        List<String> strengths = new ArrayList<>();
        List<String> weakAreas = new ArrayList<>();
        List<String> nextQuizzes = new ArrayList<>();

        resultsByTopic.forEach((topic, topicResults) -> {
            double average = topicResults.stream()
                    .mapToDouble(item -> (double) item.score() / item.totalQuestions())
                    .average()
                    .orElse(0.0);

            if (average >= 0.8) {
                strengths.add("Strong performance in " + topic);
            } else if (average < 0.6) {
                weakAreas.add("Focus more on " + topic);
                nextQuizzes.add(topic + " (Review Basics)");
            } else {
                nextQuizzes.add(topic + " (Advanced Practice)");
            }
        });

        if (strengths.isEmpty()) strengths.add("Keep practicing to identify strengths");
        if (weakAreas.isEmpty()) weakAreas.add("No specific weak areas identified yet");
        if (nextQuizzes.isEmpty()) nextQuizzes.add("Try a new domain or topic");

        return new RecommendationResponse(userId, strengths, weakAreas, nextQuizzes);
    }
}
