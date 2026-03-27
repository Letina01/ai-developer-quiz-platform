package com.aidevquiz.quiz.client;

import com.aidevquiz.quiz.dto.AiQuestionResponse;
import com.aidevquiz.quiz.dto.GenerateQuizRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service")
public interface AiServiceClient {

    @PostMapping("/api/ai/quizzes/generate")
    List<AiQuestionResponse> generateQuiz(@RequestBody GenerateQuizRequest request);
}
