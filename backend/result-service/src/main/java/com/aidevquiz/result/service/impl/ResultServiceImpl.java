package com.aidevquiz.result.service.impl;

import com.aidevquiz.result.client.EmailClient;
import com.aidevquiz.result.client.QuizAnswerQuestion;
import com.aidevquiz.result.client.QuizAnswerResponse;
import com.aidevquiz.result.client.QuizClient;
import com.aidevquiz.result.client.QuizResultEmailRequest;
import com.aidevquiz.result.dto.CreateResultRequest;
import com.aidevquiz.result.dto.ResultResponse;
import com.aidevquiz.result.entity.Result;
import com.aidevquiz.result.repository.ResultRepository;
import com.aidevquiz.result.service.ResultService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResultServiceImpl implements ResultService {

    private static final Logger log = LoggerFactory.getLogger(ResultServiceImpl.class);

    private final ResultRepository resultRepository;
    private final EmailClient emailClient;
    private final QuizClient quizClient;

    public ResultServiceImpl(ResultRepository resultRepository, EmailClient emailClient, QuizClient quizClient) {
        this.resultRepository = resultRepository;
        this.emailClient = emailClient;
        this.quizClient = quizClient;
    }

    @Override
    public ResultResponse create(Long authenticatedUserId, CreateResultRequest request) {
        QuizAnswerResponse quiz = quizClient.getQuizWithAnswers(request.quizId(), authenticatedUserId);
        if (quiz == null || quiz.questions() == null || quiz.questions().isEmpty()) {
            throw new IllegalArgumentException("Quiz not found or empty");
        }

        int totalQuestions = quiz.questions().size();
        if (request.selectedAnswers().size() != totalQuestions) {
            throw new IllegalArgumentException("Selected answers count does not match total quiz questions");
        }

        int score = calculateScore(quiz.questions(), request.selectedAnswers());
        List<ResultResponse.ReviewItem> review = buildReview(quiz.questions(), request.selectedAnswers());

        Result result = new Result();
        result.setUserId(authenticatedUserId);
        result.setQuizId(quiz.id());
        result.setDomain(quiz.domain());
        result.setTopic(quiz.topic());
        result.setDifficulty(quiz.difficulty());
        result.setScore(score);
        result.setTotalQuestions(totalQuestions);
        Result saved = resultRepository.save(result);
        
        sendEmailNotification(saved, request.userEmail(), request.userName(), request.recommendations());
        
        return mapWithReview(saved, review);
    }

    private int calculateScore(List<QuizAnswerQuestion> questions, List<String> selectedAnswers) {
        int score = 0;
        for (int index = 0; index < questions.size(); index++) {
            String expected = normalize(questions.get(index).correctAnswer());
            String selected = normalize(selectedAnswers.get(index));
            boolean correct = !expected.isBlank() && expected.equalsIgnoreCase(selected);
            log.debug("Q{} - Expected: '{}', Selected: '{}', Correct: {}", 
                    index + 1, expected, selected, correct);
            if (correct) {
                score++;
            }
        }
        return score;
    }

    private List<ResultResponse.ReviewItem> buildReview(List<QuizAnswerQuestion> questions, List<String> selectedAnswers) {
        List<ResultResponse.ReviewItem> review = new ArrayList<>();
        for (int index = 0; index < questions.size(); index++) {
            QuizAnswerQuestion question = questions.get(index);
            String expected = normalize(question.correctAnswer());
            String selected = normalize(selectedAnswers.get(index));
            boolean correct = !expected.isBlank() && expected.equalsIgnoreCase(selected);
            log.debug("Building review Q{} - Expected: '{}', Selected: '{}', Correct: {}, Explanation: {}", 
                    index + 1, expected, selected, correct, 
                    question.explanation() != null ? "YES" : "NO");
            review.add(new ResultResponse.ReviewItem(
                    question.question(),
                    selected.isEmpty() ? "Not answered" : selected,
                    expected,
                    correct,
                    question.explanation() != null ? question.explanation() : "No explanation available"
            ));
        }
        return review;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private void sendEmailNotification(Result result, String userEmail, String userName, String recommendations) {
        try {
            QuizResultEmailRequest emailRequest = new QuizResultEmailRequest(
                userEmail,
                userName != null ? userName : "User",
                result.getTopic() + " Quiz",
                result.getTotalQuestions(),
                result.getScore(),
                result.getTotalQuestions() > 0 ? (result.getScore() * 100.0 / result.getTotalQuestions()) : 0,
                recommendations
            );
            emailClient.sendQuizResultEmail(emailRequest);
        } catch (Exception e) {
            log.warn("Failed to send email notification: {}", e.getMessage());
        }
    }

    @Override
    public List<ResultResponse> getByUser(Long userId) {
        return resultRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::map).toList();
    }

    private ResultResponse map(Result result) {
        return new ResultResponse(
                result.getId(),
                result.getUserId(),
                result.getQuizId(),
                result.getDomain(),
                result.getTopic(),
                result.getDifficulty(),
                result.getScore(),
                result.getTotalQuestions(),
                result.getCreatedAt(),
                List.of()
        );
    }

    private ResultResponse mapWithReview(Result result, List<ResultResponse.ReviewItem> review) {
        return new ResultResponse(
                result.getId(),
                result.getUserId(),
                result.getQuizId(),
                result.getDomain(),
                result.getTopic(),
                result.getDifficulty(),
                result.getScore(),
                result.getTotalQuestions(),
                result.getCreatedAt(),
                review
        );
    }
}
