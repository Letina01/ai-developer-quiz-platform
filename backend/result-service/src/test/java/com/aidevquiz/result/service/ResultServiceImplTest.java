package com.aidevquiz.result.service;

import com.aidevquiz.result.client.EmailClient;
import com.aidevquiz.result.client.QuizAnswerQuestion;
import com.aidevquiz.result.client.QuizAnswerResponse;
import com.aidevquiz.result.client.QuizClient;
import com.aidevquiz.result.client.QuizResultEmailRequest;
import com.aidevquiz.result.dto.CreateResultRequest;
import com.aidevquiz.result.dto.ResultResponse;
import com.aidevquiz.result.entity.Result;
import com.aidevquiz.result.repository.ResultRepository;
import com.aidevquiz.result.service.impl.ResultServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultServiceImplTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private EmailClient emailClient;

    @Mock
    private QuizClient quizClient;

    @InjectMocks
    private ResultServiceImpl resultService;

    private QuizAnswerResponse quizResponse;
    private Result savedResult;
    private CreateResultRequest createRequest;

    @BeforeEach
    void setUp() {
        List<QuizAnswerQuestion> questions = List.of(
                new QuizAnswerQuestion("Q1", "A", "Answer A is correct", "A"),
                new QuizAnswerQuestion("Q2", "B", "Answer B is correct", "B"),
                new QuizAnswerQuestion("Q3", "C", "Answer C is correct", "C")
        );

        quizResponse = new QuizAnswerResponse(
                1L,
                "Java",
                "Spring Boot",
                "Intermediate",
                questions
        );

        savedResult = new Result();
        savedResult.setId(1L);
        savedResult.setUserId(100L);
        savedResult.setQuizId(1L);
        savedResult.setDomain("Java");
        savedResult.setTopic("Spring Boot");
        savedResult.setDifficulty("Intermediate");
        savedResult.setScore(2);
        savedResult.setTotalQuestions(3);
        savedResult.setCreatedAt(Instant.now());

        createRequest = new CreateResultRequest(
                1L,
                100L,
                List.of("A", "B", "C"),
                "user@example.com",
                "Test User",
                "Practice more"
        );
    }

    @Test
    @DisplayName("Should create result with all correct answers")
    void create_AllCorrectAnswers() {
        when(quizClient.getQuizWithAnswers(1L, 100L)).thenReturn(quizResponse);
        when(resultRepository.save(any(Result.class))).thenReturn(savedResult);
        doNothing().when(emailClient).sendQuizResultEmail(any());

        ResultResponse response = resultService.create(100L, createRequest);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(3, response.totalQuestions());
        
        verify(quizClient).getQuizWithAnswers(1L, 100L);
        verify(resultRepository).save(any(Result.class));
        verify(emailClient).sendQuizResultEmail(any(QuizResultEmailRequest.class));
    }

    @Test
    @DisplayName("Should create result with partial correct answers")
    void create_PartialCorrectAnswers() {
        List<QuizAnswerQuestion> questions = List.of(
                new QuizAnswerQuestion("Q1", "A", "Answer A is correct", "A"),
                new QuizAnswerQuestion("Q2", "B", "Answer B is correct", "B"),
                new QuizAnswerQuestion("Q3", "C", "Answer C is correct", "WRONG")
        );

        QuizAnswerResponse quizWithPartial = new QuizAnswerResponse(
                1L, "Java", "Spring Boot", "Intermediate", questions
        );

        Result partialResult = new Result();
        partialResult.setId(2L);
        partialResult.setUserId(100L);
        partialResult.setQuizId(1L);
        partialResult.setScore(2);
        partialResult.setTotalQuestions(3);

        CreateResultRequest partialRequest = new CreateResultRequest(
                1L, 100L, List.of("A", "B", "WRONG"),
                "user@example.com", "Test User", "Practice more"
        );

        when(quizClient.getQuizWithAnswers(1L, 100L)).thenReturn(quizWithPartial);
        when(resultRepository.save(any(Result.class))).thenReturn(partialResult);
        doNothing().when(emailClient).sendQuizResultEmail(any());

        ResultResponse response = resultService.create(100L, partialRequest);

        assertNotNull(response);
        assertEquals(2, response.id());
        
        ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
        verify(resultRepository).save(resultCaptor.capture());
        assertEquals(2, resultCaptor.getValue().getScore());
    }

    @Test
    @DisplayName("Should throw exception when quiz is null")
    void create_NullQuiz_ThrowsException() {
        when(quizClient.getQuizWithAnswers(1L, 100L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> resultService.create(100L, createRequest));

        verify(resultRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when quiz has no questions")
    void create_EmptyQuiz_ThrowsException() {
        QuizAnswerResponse emptyQuiz = new QuizAnswerResponse(
                1L, "Java", "Spring Boot", "Intermediate", List.of()
        );
        when(quizClient.getQuizWithAnswers(1L, 100L)).thenReturn(emptyQuiz);

        assertThrows(IllegalArgumentException.class,
                () -> resultService.create(100L, createRequest));

        verify(resultRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when answer count doesn't match")
    void create_AnswerCountMismatch_ThrowsException() {
        CreateResultRequest mismatchRequest = new CreateResultRequest(
                1L, 100L, List.of("A", "B"),
                "user@example.com", "Test User", "Practice more"
        );
        when(quizClient.getQuizWithAnswers(1L, 100L)).thenReturn(quizResponse);

        assertThrows(IllegalArgumentException.class,
                () -> resultService.create(100L, mismatchRequest));

        verify(resultRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get results by user ID")
    void getByUser_Success() {
        List<Result> results = List.of(savedResult);
        when(resultRepository.findByUserIdOrderByCreatedAtDesc(100L)).thenReturn(results);

        List<ResultResponse> response = resultService.getByUser(100L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals(100L, response.get(0).userId());
        
        verify(resultRepository).findByUserIdOrderByCreatedAtDesc(100L);
    }

    @Test
    @DisplayName("Should return empty list when no results found")
    void getByUser_NoResults() {
        when(resultRepository.findByUserIdOrderByCreatedAtDesc(999L)).thenReturn(List.of());

        List<ResultResponse> response = resultService.getByUser(999L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("Should handle email failure gracefully")
    void create_EmailFailure_StillSavesResult() {
        when(quizClient.getQuizWithAnswers(1L, 100L)).thenReturn(quizResponse);
        when(resultRepository.save(any(Result.class))).thenReturn(savedResult);
        doThrow(new RuntimeException("Email server error"))
                .when(emailClient).sendQuizResultEmail(any());

        ResultResponse response = resultService.create(100L, createRequest);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(resultRepository).save(any(Result.class));
    }

    @Test
    @DisplayName("Should normalize answer comparison case-insensitively")
    void create_CaseInsensitiveComparison() {
        List<QuizAnswerQuestion> questions = List.of(
                new QuizAnswerQuestion("Q1", "A", "Answer A is correct", "a")
        );

        QuizAnswerResponse quizWithLowercase = new QuizAnswerResponse(
                1L, "Java", "Spring Boot", "Intermediate", questions
        );

        Result resultWithCaseMatch = new Result();
        resultWithCaseMatch.setId(3L);
        resultWithCaseMatch.setScore(1);
        resultWithCaseMatch.setTotalQuestions(1);

        CreateResultRequest caseRequest = new CreateResultRequest(
                1L, 100L, List.of("A"),
                "user@example.com", "Test User", "Practice more"
        );

        when(quizClient.getQuizWithAnswers(1L, 100L)).thenReturn(quizWithLowercase);
        when(resultRepository.save(any(Result.class))).thenReturn(resultWithCaseMatch);
        doNothing().when(emailClient).sendQuizResultEmail(any());

        ResultResponse response = resultService.create(100L, caseRequest);

        assertNotNull(response);
        ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
        verify(resultRepository).save(resultCaptor.capture());
        assertEquals(1, resultCaptor.getValue().getScore());
    }
}
