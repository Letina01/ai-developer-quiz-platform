package com.aidevquiz.quiz.service;

import com.aidevquiz.quiz.dto.GenerateQuizRequest;
import com.aidevquiz.quiz.dto.QuizResponse;
import java.util.List;

public interface QuizService {
    QuizResponse generateQuiz(Long userId, GenerateQuizRequest request);
    List<QuizResponse> getAll(Long userId);
    QuizResponse getById(Long quizId, Long userId);
    QuizResponse getByIdWithAnswers(Long quizId, Long userId);
    QuizResponse getByIdWithFullAnswers(Long quizId, Long userId);
}
