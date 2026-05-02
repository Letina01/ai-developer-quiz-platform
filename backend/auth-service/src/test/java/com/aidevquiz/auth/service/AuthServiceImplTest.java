package com.aidevquiz.auth.service;

import com.aidevquiz.auth.dto.AuthResponse;
import com.aidevquiz.auth.dto.LoginRequest;
import com.aidevquiz.auth.dto.RegisterRequest;
import com.aidevquiz.auth.dto.UpdateProfileRequest;
import com.aidevquiz.auth.entity.User;
import com.aidevquiz.auth.repository.UserRepository;
import com.aidevquiz.auth.security.JwtService;
import com.aidevquiz.auth.client.EmailClient;
import com.aidevquiz.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setAuthProvider("LOCAL");
        testUser.setProfileCompleted(false);

        registerRequest = new RegisterRequest(
                "Test User",
                "test@example.com",
                "password123"
        );

        loginRequest = new LoginRequest(
                "test@example.com",
                "password123"
        );
    }

    @Test
    @DisplayName("Should register new user successfully")
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(anyString(), any())).thenReturn("jwt-token");
        doNothing().when(emailClient).sendWelcomeEmail(any());

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals(testUser.getName(), response.name());
        assertEquals(testUser.getEmail(), response.email());
        assertEquals("jwt-token", response.token());

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(emailClient).sendWelcomeEmail(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_EmailExists_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUser.getEmail(), testUser.getPassword()));
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(anyString(), any())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals(testUser.getEmail(), response.email());
        assertEquals("jwt-token", response.token());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(eq(testUser.getEmail()), any());
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void login_InvalidCredentials_ThrowsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(anyString(), any());
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_Success() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testUser));

        User found = authService.findByEmail("test@example.com");

        assertNotNull(found);
        assertEquals(testUser.getEmail(), found.getEmail());
        verify(userRepository).findByEmailIgnoreCase("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception when user not found by email")
    void findByEmail_NotFound_ThrowsException() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> authService.findByEmail("nonexistent@example.com")
        );
    }

    @Test
    @DisplayName("Should find user by ID")
    void findById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User found = authService.findById(1L);

        assertNotNull(found);
        assertEquals(testUser.getId(), found.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void findById_NotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> authService.findById(999L)
        );
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void updateProfile_Success() {
        UpdateProfileRequest updateRequest = new UpdateProfileRequest(
                "Updated Name",
                "Java",
                "Backend Developer",
                "Intermediate",
                "Java, Spring",
                "Master Spring Boot"
        );

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("test@example.com");
        updatedUser.setFocusDomain("Java");
        updatedUser.setTargetRole("Backend Developer");
        updatedUser.setExperienceLevel("Intermediate");
        updatedUser.setCurrentSkills("Java, Spring");
        updatedUser.setStudyGoal("Master Spring Boot");
        updatedUser.setProfileCompleted(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = authService.updateProfile(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Java", result.getFocusDomain());
        assertTrue(result.isProfileCompleted());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("Updated Name", savedUser.getName());
        assertEquals("Java", savedUser.getFocusDomain());
    }

    @Test
    @DisplayName("Should find or create Google user - existing user")
    void findOrCreateGoogleUser_ExistingUser() {
        testUser.setAuthProvider("GOOGLE");
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = authService.findOrCreateGoogleUser("test@example.com", "Test User");

        assertNotNull(result);
        assertEquals("GOOGLE", result.getAuthProvider());
        verify(userRepository).findByEmailIgnoreCase("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(emailClient, never()).sendWelcomeEmail(any());
    }

    @Test
    @DisplayName("Should find or create Google user - new user")
    void findOrCreateGoogleUser_NewUser() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("randomPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(emailClient).sendWelcomeEmail(any());

        User result = authService.findOrCreateGoogleUser("newuser@example.com", "New User");

        assertNotNull(result);
        verify(userRepository).findByEmailIgnoreCase("newuser@example.com");
        verify(userRepository).save(any(User.class));
        verify(emailClient).sendWelcomeEmail(any());
    }

    @Test
    @DisplayName("Should build auth response correctly")
    void buildAuthResponse_Success() {
        testUser.setProfileCompleted(true);
        when(jwtService.generateToken(anyString(), any())).thenReturn("jwt-token");

        AuthResponse response = authService.buildAuthResponse(testUser);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals(testUser.getName(), response.name());
        assertEquals(testUser.getEmail(), response.email());
        assertTrue(response.profileCompleted());
        assertEquals("jwt-token", response.token());

        verify(jwtService).generateToken(
                eq(testUser.getEmail()),
                eq(Map.of(
                        "userId", testUser.getId(),
                        "name", testUser.getName(),
                        "profileCompleted", testUser.isProfileCompleted()
                ))
        );
    }

    @Test
    @DisplayName("Should validate reset token - valid token")
    void validateResetToken_ValidToken() {
        testUser.setResetToken("valid-token");
        testUser.setResetTokenExpiresAt(java.time.Instant.now().plusSeconds(3600));
        when(userRepository.findByResetToken("valid-token")).thenReturn(Optional.of(testUser));

        boolean isValid = authService.validateResetToken("valid-token");

        assertTrue(isValid);
        verify(userRepository).findByResetToken("valid-token");
    }

    @Test
    @DisplayName("Should validate reset token - expired token")
    void validateResetToken_ExpiredToken() {
        testUser.setResetToken("expired-token");
        testUser.setResetTokenExpiresAt(java.time.Instant.now().minusSeconds(3600));
        when(userRepository.findByResetToken("expired-token")).thenReturn(Optional.of(testUser));

        boolean isValid = authService.validateResetToken("expired-token");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should validate reset token - null token")
    void validateResetToken_NullToken() {
        boolean isValid = authService.validateResetToken(null);

        assertFalse(isValid);
        verify(userRepository, never()).findByResetToken(anyString());
    }

    @Test
    @DisplayName("Should reset password successfully")
    void resetPassword_Success() {
        testUser.setResetToken("reset-token");
        testUser.setResetTokenExpiresAt(java.time.Instant.now().plusSeconds(3600));
        when(userRepository.findByResetToken("reset-token")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = authService.resetPassword("reset-token", "newPassword123");

        assertTrue(result);
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(any(User.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNull(userCaptor.getValue().getResetToken());
        assertNull(userCaptor.getValue().getResetTokenExpiresAt());
    }

    @Test
    @DisplayName("Should load user by username")
    void loadUserByUsername_Success() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testUser));

        var userDetails = authService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals(testUser.getEmail(), userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Should handle email case insensitivity")
    void register_EmailCaseInsensitive() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(anyString(), any())).thenReturn("jwt-token");
        doNothing().when(emailClient).sendWelcomeEmail(any());

        RegisterRequest upperCaseRequest = new RegisterRequest(
                "Test User",
                "TEST@EXAMPLE.COM",
                "password123"
        );

        AuthResponse response = authService.register(upperCaseRequest);

        assertNotNull(response);
        verify(userRepository).existsByEmail("test@example.com");
    }
}
