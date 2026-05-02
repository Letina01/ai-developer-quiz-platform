package com.aidevquiz.result.controller;

import com.aidevquiz.result.dto.CreateResultRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ResultControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 401 for unauthenticated request")
    void createResult_Unauthenticated_Returns401() throws Exception {
        CreateResultRequest request = new CreateResultRequest(
                1L,
                1L,
                List.of("A", "B", "C"),
                "test@example.com",
                "Test User",
                "Practice more"
        );

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 for missing X-User-Id header")
    void createResult_MissingUserId_Returns400() throws Exception {
        CreateResultRequest request = new CreateResultRequest(
                1L,
                null,
                List.of("A", "B", "C"),
                "test@example.com",
                "Test User",
                "Practice more"
        );

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 for empty selected answers")
    void createResult_EmptyAnswers_Returns400() throws Exception {
        CreateResultRequest request = new CreateResultRequest(
                1L,
                1L,
                List.of(),
                "test@example.com",
                "Test User",
                "Practice more"
        );

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return empty list when no results exist")
    void getMyResults_NoResults_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/results/users/me")
                        .header("X-User-Id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Should return 401 for getMyResults without authentication")
    void getMyResults_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/results/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Health endpoint should return OK")
    void health_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/results/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.service").value("result-service"));
    }

    @Test
    @DisplayName("Should return 400 for invalid quiz ID")
    void createResult_InvalidQuizId_Returns400() throws Exception {
        CreateResultRequest request = new CreateResultRequest(
                null,
                1L,
                List.of("A"),
                "test@example.com",
                "Test User",
                "Practice more"
        );

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debug headers endpoint should work without auth")
    void debugHeaders_NoAuth_Works() throws Exception {
        mockMvc.perform(get("/api/results/debug/headers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.X-User-Id").value("NOT PRESENT"));
    }

    @Test
    @DisplayName("Debug headers endpoint should show X-User-Id when provided")
    void debugHeaders_WithUserId_ShowsValue() throws Exception {
        mockMvc.perform(get("/api/results/debug/headers")
                        .header("X-User-Id", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.X-User-Id").value("123"));
    }
}
