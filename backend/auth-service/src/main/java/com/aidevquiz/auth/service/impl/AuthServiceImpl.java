package com.aidevquiz.auth.service.impl;

import com.aidevquiz.auth.dto.AuthResponse;
import com.aidevquiz.auth.dto.LoginRequest;
import com.aidevquiz.auth.dto.RegisterRequest;
import com.aidevquiz.auth.dto.UpdateProfileRequest;
import com.aidevquiz.auth.entity.User;
import com.aidevquiz.auth.repository.UserRepository;
import com.aidevquiz.auth.security.JwtService;
import com.aidevquiz.auth.service.AuthService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Lazy AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = findByEmail(request.email());
        return buildAuthResponse(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User findOrCreateGoogleUser(String email, String name) {
        return userRepository.findByEmail(email.toLowerCase())
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
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setAuthProvider("GOOGLE");
                    return userRepository.save(user);
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
    public UserDetails loadUserByUsername(String username) {
        User user = findByEmail(username);
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
}
