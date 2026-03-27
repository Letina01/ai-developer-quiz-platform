package com.aidevquiz.auth.controller;

import com.aidevquiz.auth.dto.AuthResponse;
import com.aidevquiz.auth.dto.LoginRequest;
import com.aidevquiz.auth.dto.ProfileResponse;
import com.aidevquiz.auth.dto.RegisterRequest;
import com.aidevquiz.auth.dto.UpdateProfileRequest;
import com.aidevquiz.auth.entity.User;
import com.aidevquiz.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public ProfileResponse me(@AuthenticationPrincipal UserDetails principal) {
        User user = authService.findByEmail(principal.getUsername());
        return toProfileResponse(user);
    }

    @PutMapping("/profile")
    public ProfileResponse updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        User existingUser = authService.findByEmail(principal.getUsername());
        User updatedUser = authService.updateProfile(existingUser.getId(), request);
        return toProfileResponse(updatedUser);
    }

    private ProfileResponse toProfileResponse(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAuthProvider(),
                user.getFocusDomain(),
                user.getTargetRole(),
                user.getExperienceLevel(),
                user.getCurrentSkills(),
                user.getStudyGoal(),
                user.isProfileCompleted()
        );
    }
}
