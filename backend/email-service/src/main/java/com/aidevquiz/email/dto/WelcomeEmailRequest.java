package com.aidevquiz.email.dto;

public record WelcomeEmailRequest(
        String to,
        String userName
) {
}
