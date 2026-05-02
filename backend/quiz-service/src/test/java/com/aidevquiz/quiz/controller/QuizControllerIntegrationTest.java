package com.aidevquiz.quiz.controller;

import com.aidevquiz.quiz.dto.GenerateQuizRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuizControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 401 for unauthenticated request")
    void generateQuiz_Unauthenticated_Returns401() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest(
                "Java",
                "Beginner",
                5,
                "Core Java"
        );

        mockMvc.perform(post("/api/quizzes/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 for missing required fields")
    void generateQuiz_MissingFields_Returns400() throws Exception {
        String invalidRequest = "{}";

        mockMvc.perform(post("/api/quizzes/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest)
                        .header("X-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid technology")
    void generateQuiz_InvalidTechnology_Returns400() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest(
                "",
                "Beginner",
                5,
                "Core Java"
        );

        mockMvc.perform(post("/api/quizzes/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid question count")
    void generateQuiz_InvalidQuestionCount_Returns400() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest(
                "Java",
                "Beginner",
                0,
                "Core Java"
        );

        mockMvc.perform(post("/api/quizzes/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid difficulty level")
    void generateQuiz_InvalidDifficulty_Returns400() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest(
                "Java",
                "Invalid",
                5,
                "Core Java"
        );

        mockMvc.perform(post("/api/quizzes/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get all quizzes for authenticated user")
    void getAll_Authenticated_Success() throws Exception {
        mockMvc.perform(get("/api/quizzes")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 for get all without authentication")
    void getAll_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/quizzes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return empty list when no quizzes exist")
    void getAll_NoQuizzes_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/quizzes")
                        .header("X-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return 400 for negative question count")
    void generateQuiz_NegativeQuestionCount_Returns400() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest(
                "Java",
                "Beginner",
                -5,
                "Core Java"
        );

        mockMvc.perform(post("/api/quizzes/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }
}
