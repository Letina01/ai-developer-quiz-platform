package com.aidevquiz.quiz.controller;

import com.aidevquiz.quiz.dto.GenerateQuizRequest;
import com.aidevquiz.quiz.dto.QuizResponse;
import com.aidevquiz.quiz.service.QuizService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/generate")
    public QuizResponse generateQuiz(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody GenerateQuizRequest request
    ) {
        return quizService.generateQuiz(userId, request);
    }

    @GetMapping
    public List<QuizResponse> getAll() {
        return quizService.getAll();
    }

    @GetMapping("/{quizId}")
    public QuizResponse getById(@PathVariable("quizId") Long quizId) {
        return quizService.getById(quizId);
    }
}
