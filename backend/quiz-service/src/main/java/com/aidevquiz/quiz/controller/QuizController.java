package com.aidevquiz.quiz.controller;

import com.aidevquiz.quiz.dto.GenerateQuizRequest;
import com.aidevquiz.quiz.dto.QuizResponse;
import com.aidevquiz.quiz.service.QuizService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private static final Logger log = LoggerFactory.getLogger(QuizController.class);

    private final QuizService quizService;
    private final String internalApiKey;

    public QuizController(
            QuizService quizService,
            @Value("${app.internal-api-key:change-me-internal}") String internalApiKey
    ) {
        this.quizService = quizService;
        this.internalApiKey = internalApiKey;
    }

    @PostMapping("/generate")
    public QuizResponse generateQuiz(
            @RequestHeader(value = "X-User-Id", required = true) Long userId,
            @Valid @RequestBody GenerateQuizRequest request
    ) {
        return quizService.generateQuiz(userId, request);
    }

    @GetMapping
    public List<QuizResponse> getAll(@RequestHeader(value = "X-User-Id", required = true) Long userId) {
        return quizService.getAll(userId);
    }

    @GetMapping("/{quizId}")
    public QuizResponse getById(
            @PathVariable("quizId") Long quizId,
            @RequestHeader(value = "X-User-Id", required = true) Long userId
    ) {
        return quizService.getById(quizId, userId);
    }

    @GetMapping("/internal/{quizId}/answers")
    public QuizResponse getByIdWithAnswers(
            @PathVariable("quizId") Long quizId,
            @RequestHeader(value = "X-User-Id", required = true) Long userId,
            @RequestHeader(value = "X-Internal-Api-Key", required = true) String providedInternalApiKey
    ) {
        if (!internalApiKey.equals(providedInternalApiKey)) {
            throw new IllegalArgumentException("Unauthorized internal API access");
        }
        log.info("getByIdWithAnswers called for quizId: {}, userId: {}", quizId, userId);
        QuizResponse response = quizService.getByIdWithFullAnswers(quizId, userId);
        log.info("Returning {} questions with full answers", 
                response.questions() != null ? response.questions().size() : 0);
        if (response.questions() != null && !response.questions().isEmpty()) {
            var firstQ = response.questions().get(0);
            log.debug("First Q - question: {}, correctAnswer: {}, explanation: {}", 
                    firstQ.question(), 
                    firstQ.correctAnswer(), 
                    firstQ.explanation() != null ? firstQ.explanation().substring(0, Math.min(50, firstQ.explanation().length())) : "NULL");
        }
        return response;
    }
}
