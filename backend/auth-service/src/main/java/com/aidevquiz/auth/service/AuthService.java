package com.aidevquiz.auth.service;

import com.aidevquiz.auth.dto.AuthResponse;
import com.aidevquiz.auth.dto.LoginRequest;
import com.aidevquiz.auth.dto.RegisterRequest;
import com.aidevquiz.auth.dto.UpdateProfileRequest;
import com.aidevquiz.auth.entity.User;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse buildAuthResponse(User user);
    User findByEmail(String email);
    User findById(Long userId);
    User findOrCreateGoogleUser(String email, String name);
    User updateProfile(Long userId, UpdateProfileRequest request);
}
