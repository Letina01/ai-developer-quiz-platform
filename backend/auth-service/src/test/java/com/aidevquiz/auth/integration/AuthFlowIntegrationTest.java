package com.aidevquiz.auth.integration;

import com.aidevquiz.auth.dto.AuthResponse;
import com.aidevquiz.auth.dto.LoginRequest;
import com.aidevquiz.auth.dto.RegisterRequest;
import com.aidevquiz.auth.entity.User;
import com.aidevquiz.auth.repository.UserRepository;
import com.aidevquiz.auth.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete user journey: Register -> Login -> Access Protected Resource")
    void completeUserJourney() throws Exception {
        String uniqueEmail = "journey_" + System.currentTimeMillis() + "@example.com";
        
        RegisterRequest registerRequest = new RegisterRequest(
                "Journey Test User",
                uniqueEmail,
                "SecurePassword123!"
        );

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Journey Test User"))
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        AuthResponse registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), AuthResponse.class);
        String token = registerResponse.token();
        assertNotNull(token);
        assertTrue(token.length() > 20);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest(uniqueEmail, "SecurePassword123!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        User savedUser = userRepository.findByEmailIgnoreCase(uniqueEmail).orElse(null);
        assertNotNull(savedUser);
        assertEquals("Journey Test User", savedUser.getName());
        assertTrue(savedUser.getPassword().startsWith("$2"));
    }

    @Test
    @DisplayName("JWT token should contain correct claims")
    void jwtTokenContainsCorrectClaims() throws Exception {
        String email = "claims_" + System.currentTimeMillis() + "@example.com";
        
        RegisterRequest request = new RegisterRequest(
                "Claims Test User",
                email,
                "Password123!"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);

        var claims = jwtService.extractAllClaims(response.token());
        assertEquals(email, claims.get("email"));
        assertEquals("Claims Test User", claims.get("name"));
        assertNotNull(claims.get("userId"));
    }

    @Test
    @DisplayName("Login should work with email in uppercase")
    void loginWithUppercaseEmail() throws Exception {
        String email = "uppercase_" + System.currentTimeMillis() + "@example.com";
        
        RegisterRequest registerRequest = new RegisterRequest(
                "Uppercase Test",
                email,
                "Password123!"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest(
                email.toUpperCase(),
                "Password123!"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email.toLowerCase()));
    }

    @Test
    @DisplayName("Same email cannot register twice")
    void duplicateRegistrationBlocked() throws Exception {
        String duplicateEmail = "duplicate_" + System.currentTimeMillis() + "@example.com";
        
        RegisterRequest request1 = new RegisterRequest(
                "First User",
                duplicateEmail,
                "Password123!"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        RegisterRequest request2 = new RegisterRequest(
                "Second User",
                duplicateEmail,
                "DifferentPassword!"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already registered"));

        long userCount = userRepository.count();
        assertEquals(1, userCount);
    }

    @Test
    @DisplayName("Invalid credentials should not expose user existence")
    void invalidCredentialsSecurity() throws Exception {
        String existingEmail = "existing_" + System.currentTimeMillis() + "@example.com";
        
        RegisterRequest request = new RegisterRequest(
                "Existing User",
                existingEmail,
                "CorrectPassword!"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        LoginRequest wrongPasswordRequest = new LoginRequest(
                existingEmail,
                "WrongPassword!"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized());

        LoginRequest nonExistentRequest = new LoginRequest(
                "nonexistent_" + System.currentTimeMillis() + "@example.com",
                "AnyPassword!"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Password should be properly hashed")
    void passwordIsHashed() throws Exception {
        String email = "hash_" + System.currentTimeMillis() + "@example.com";
        String rawPassword = "MySecurePassword123!";
        
        RegisterRequest request = new RegisterRequest(
                "Hash Test User",
                email,
                rawPassword
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByEmailIgnoreCase(email).orElseThrow();
        
        assertNotEquals(rawPassword, savedUser.getPassword());
        assertTrue(savedUser.getPassword().startsWith("$2"));
        assertTrue(savedUser.getPassword().length() > 50);
    }
}
