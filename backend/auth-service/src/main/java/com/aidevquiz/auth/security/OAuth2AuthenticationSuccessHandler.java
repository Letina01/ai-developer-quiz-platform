package com.aidevquiz.auth.security;

import com.aidevquiz.auth.dto.AuthResponse;
import com.aidevquiz.auth.entity.User;
import com.aidevquiz.auth.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(
            AuthService authService,
            @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl
    ) {
        this.authService = authService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (email == null || email.isBlank()) {
            response.sendRedirect(frontendUrl + "/login?oauthError=missing_email");
            return;
        }

        User user = authService.findOrCreateGoogleUser(email, name);
        AuthResponse authResponse = authService.buildAuthResponse(user);

        String redirectUrl = frontendUrl + "/auth/callback"
                + "?token=" + encode(authResponse.accessToken())
                + "&userId=" + authResponse.userId()
                + "&name=" + encode(authResponse.name())
                + "&email=" + encode(authResponse.email())
                + "&profileCompleted=" + authResponse.profileCompleted()
                + "&focusDomain=" + encode(nullSafe(authResponse.focusDomain()))
                + "&targetRole=" + encode(nullSafe(authResponse.targetRole()))
                + "&authProvider=" + encode("GOOGLE");

        response.sendRedirect(redirectUrl);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
