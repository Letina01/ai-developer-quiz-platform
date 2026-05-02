package com.aidevquiz.auth.service.impl;

import com.aidevquiz.auth.dto.AuthResponse;
import com.aidevquiz.auth.dto.LoginRequest;
import com.aidevquiz.auth.dto.RegisterRequest;
import com.aidevquiz.auth.dto.UpdateProfileRequest;
import com.aidevquiz.auth.entity.User;
import com.aidevquiz.auth.repository.UserRepository;
import com.aidevquiz.auth.security.JwtService;
import com.aidevquiz.auth.service.AuthService;
import com.aidevquiz.auth.client.EmailClient;
import com.aidevquiz.auth.client.WelcomeEmailRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailClient emailClient;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Lazy AuthenticationManager authenticationManager,
            JwtService jwtService,
            EmailClient emailClient
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailClient = emailClient;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);
        sendWelcomeEmail(saved);
        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().toLowerCase();
        log.info("Login attempt for: {}", normalizedEmail);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, request.password()));
        User user = findByEmail(normalizedEmail);
        log.info("Login successful for: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User findOrCreateGoogleUser(String email, String name) {
        String normalizedEmail = email.toLowerCase();
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .map(existingUser -> {
                    existingUser.setAuthProvider("GOOGLE");
                    if (existingUser.getName() == null || existingUser.getName().isBlank()) {
                        existingUser.setName(name == null || name.isBlank() ? email : name);
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User user = new User();
                    user.setName(name == null || name.isBlank() ? email : name);
                    user.setEmail(normalizedEmail);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setAuthProvider("GOOGLE");
                    User created = userRepository.save(user);
                    sendWelcomeEmail(created);
                    return created;
                });
    }

    @Override
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findById(userId);
        user.setName(request.name().trim());
        user.setFocusDomain(request.focusDomain().trim());
        user.setTargetRole(request.targetRole().trim());
        user.setExperienceLevel(request.experienceLevel().trim());
        user.setCurrentSkills(request.currentSkills().trim());
        user.setStudyGoal(request.studyGoal().trim());
        user.setProfileCompleted(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPasswordByEmail(String email, String password) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        String encodedPassword = passwordEncoder.encode(password);
        log.debug("Encoding password for: {}, raw length: {}", email, password.length());
        user.setPassword(encodedPassword);
        User saved = userRepository.save(user);
        log.info("Password reset successful for user: {}", saved.getEmail());
    }

    @Override
    public boolean validateResetToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        return userRepository.findByResetToken(token)
                .filter(user -> user.getResetTokenExpiresAt() != null)
                .filter(user -> user.getResetTokenExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }

    @Override
    public boolean resetPassword(String token, String password) {
        User user = userRepository.findByResetToken(token)
                .filter(item -> item.getResetTokenExpiresAt() != null && item.getResetTokenExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        String normalizedUsername = username.toLowerCase();
        log.debug("loadUserByUsername called with: {}", normalizedUsername);
        User user = findByEmail(normalizedUsername);
        log.debug("User found: {}, password length: {}", user.getEmail(), user.getPassword().length());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of(
                        "userId", user.getId(),
                        "name", user.getName(),
                        "profileCompleted", user.isProfileCompleted()
                )
        );
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isProfileCompleted(),
                user.getFocusDomain(),
                user.getTargetRole(),
                token
        );
    }

    private void sendWelcomeEmail(User user) {
        emailClient.sendWelcomeEmail(new WelcomeEmailRequest(
                user.getEmail(),
                user.getName()
        ));
    }
}
