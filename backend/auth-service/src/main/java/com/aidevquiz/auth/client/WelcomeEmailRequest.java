package com.aidevquiz.auth.client;

public record WelcomeEmailRequest(
        String to,
        String userName
) {
}
