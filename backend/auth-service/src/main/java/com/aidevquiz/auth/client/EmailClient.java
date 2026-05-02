package com.aidevquiz.auth.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmailClient {

    private static final Logger log = LoggerFactory.getLogger(EmailClient.class);

    private final RestTemplate restTemplate;
    private final String emailServiceUrl;

    public EmailClient(@Value("${EMAIL_SERVICE_URL:http://email-service:8086}") String emailServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.emailServiceUrl = emailServiceUrl;
    }

    public void sendWelcomeEmail(WelcomeEmailRequest request) {
        try {
            restTemplate.postForEntity(emailServiceUrl + "/api/email/welcome", request, String.class);
            log.info("Welcome email notification sent successfully");
        } catch (Exception exception) {
            log.error("Failed to send welcome email notification: {}", exception.getMessage());
        }
    }
}
