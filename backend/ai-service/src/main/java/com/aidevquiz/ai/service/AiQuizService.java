package com.aidevquiz.ai.service;

import com.aidevquiz.ai.dto.AiQuestionResponse;
import com.aidevquiz.ai.dto.GenerateQuizRequest;
import java.util.List;

public interface AiQuizService {
    List<AiQuestionResponse> generateQuestions(GenerateQuizRequest request);
}
