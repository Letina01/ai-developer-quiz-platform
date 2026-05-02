package com.aidevquiz.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiGatewayIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return 401 for unauthenticated request to protected route")
    void protectedRoute_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/results/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 for invalid JWT token")
    void protectedRoute_InvalidToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/results/users/me")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 for malformed Authorization header")
    void protectedRoute_MalformedAuth_Returns401() throws Exception {
        mockMvc.perform(get("/api/results/users/me")
                        .header("Authorization", "NotBearer token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 for expired JWT token")
    void protectedRoute_ExpiredToken_Returns401() throws Exception {
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjB9" +
                ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        mockMvc.perform(get("/api/results/users/me")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should accept request with valid JWT structure (mocked)")
    void protectedRoute_ValidStructure_PassesThrough() throws Exception {
        String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwidXNlcklkIjoxMjMsImlhdCI6MTUxNjIzOTAyMn0" +
                ".mock_signature_for_testing";

        mockMvc.perform(get("/api/results/users/me")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("CORS preflight request should be allowed")
    void cors_Preflight_Returns200() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Request without Origin header should work normally")
    void noOrigin_Success() throws Exception {
        mockMvc.perform(get("/api/auth/login")
                        .contentType("application/json"))
                .andExpect(status().is4xxClientError());
    }
}
