package com.aidevquiz.quiz.service.impl;

import com.aidevquiz.quiz.client.AiServiceClient;
import com.aidevquiz.quiz.dto.AiQuestionResponse;
import com.aidevquiz.quiz.dto.GenerateQuizRequest;
import com.aidevquiz.quiz.dto.QuestionResponse;
import com.aidevquiz.quiz.dto.QuizResponse;
import com.aidevquiz.quiz.entity.Question;
import com.aidevquiz.quiz.entity.Quiz;
import com.aidevquiz.quiz.repository.QuizRepository;
import com.aidevquiz.quiz.service.QuizService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final AiServiceClient aiServiceClient;

    public QuizServiceImpl(QuizRepository quizRepository, AiServiceClient aiServiceClient) {
        this.quizRepository = quizRepository;
        this.aiServiceClient = aiServiceClient;
    }

    @Override
    public QuizResponse generateQuiz(Long userId, GenerateQuizRequest request) {
        List<AiQuestionResponse> generatedQuestions = aiServiceClient.generateQuiz(request);
        Quiz quiz = new Quiz();
        quiz.setCreatedBy(userId);
        quiz.setDomain(request.domain());
        quiz.setTopic(request.topic());
        quiz.setDifficulty(request.difficulty());

        generatedQuestions.forEach(item -> {
            validateAiQuestion(item);
            Question question = new Question();
            question.setQuestion(item.question());
            question.setOptionA(item.options().get(0));
            question.setOptionB(item.options().get(1));
            question.setOptionC(item.options().get(2));
            question.setOptionD(item.options().get(3));

            String correct = item.correctAnswer();
            if (correct.length() == 1) {
                switch (correct.toUpperCase()) {
                    case "0", "1", "2", "3" -> correct = item.options().get(Integer.parseInt(correct));
                    case "A" -> correct = item.options().get(0);
                    case "B" -> correct = item.options().get(1);
                    case "C" -> correct = item.options().get(2);
                    case "D" -> correct = item.options().get(3);
                }
            }
            question.setCorrectAnswer(correct);

            question.setExplanation(item.explanation());
            quiz.addQuestion(question);
        });

        return map(quizRepository.save(quiz));
    }

    private void validateAiQuestion(AiQuestionResponse item) {
        if (item.options() == null || item.options().size() != 4) {
            throw new IllegalStateException("AI service returned a question without exactly 4 options");
        }
    }

    @Override
    public List<QuizResponse> getAll() {
        return quizRepository.findAll().stream().map(this::map).toList();
    }

    @Override
    public QuizResponse getById(Long quizId) {
        return map(quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Quiz not found")));
    }

    private QuizResponse map(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getCreatedBy(),
                quiz.getDomain(),
                quiz.getTopic(),
                quiz.getDifficulty(),
                quiz.getCreatedAt(),
                quiz.getQuestions().stream()
                        .map(question -> new QuestionResponse(
                                question.getId(),
                                question.getQuestion(),
                                question.getOptionA(),
                                question.getOptionB(),
                                question.getOptionC(),
                                question.getOptionD(),
                                question.getCorrectAnswer(),
                                question.getExplanation()
                        ))
                        .toList()
        );
    }
}
