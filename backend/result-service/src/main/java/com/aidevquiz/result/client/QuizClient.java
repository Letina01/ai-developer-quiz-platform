package com.aidevquiz.result.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class QuizClient {

    private final RestTemplate restTemplate;
    private final String quizServiceUrl;
    private final String internalApiKey;

    public QuizClient(
            @Value("${app.quiz-service-url:http://localhost:8082}") String quizServiceUrl,
            @Value("${app.internal-api-key:change-me-internal}") String internalApiKey
    ) {
        this.restTemplate = new RestTemplate();
        this.quizServiceUrl = quizServiceUrl;
        this.internalApiKey = internalApiKey;
    }

    public QuizAnswerResponse getQuizWithAnswers(Long quizId, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", String.valueOf(userId));
        headers.set("X-Internal-Api-Key", internalApiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        String url = quizServiceUrl + "/api/quizzes/internal/" + quizId + "/answers";
        ResponseEntity<QuizAnswerResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                QuizAnswerResponse.class
        );

        return response.getBody();
    }
}
