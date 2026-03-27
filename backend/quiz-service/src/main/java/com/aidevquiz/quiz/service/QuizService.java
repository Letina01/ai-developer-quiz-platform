package com.aidevquiz.quiz.service;

import com.aidevquiz.quiz.dto.GenerateQuizRequest;
import com.aidevquiz.quiz.dto.QuizResponse;
import java.util.List;

public interface QuizService {
    QuizResponse generateQuiz(Long userId, GenerateQuizRequest request);
    List<QuizResponse> getAll();
    QuizResponse getById(Long quizId);
}
