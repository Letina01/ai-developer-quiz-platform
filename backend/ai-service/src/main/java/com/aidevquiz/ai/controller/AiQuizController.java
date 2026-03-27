package com.aidevquiz.ai.controller;

import com.aidevquiz.ai.dto.AiQuestionResponse;
import com.aidevquiz.ai.dto.GenerateQuizRequest;
import com.aidevquiz.ai.service.AiQuizService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/quizzes")
public class AiQuizController {

    private final AiQuizService aiQuizService;

    public AiQuizController(AiQuizService aiQuizService) {
        this.aiQuizService = aiQuizService;
    }

    @PostMapping("/generate")
    public List<AiQuestionResponse> generate(@Valid @RequestBody GenerateQuizRequest request) {
        return aiQuizService.generateQuestions(request);
    }
}
