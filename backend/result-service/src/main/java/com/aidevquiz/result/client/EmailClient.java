package com.aidevquiz.result.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmailClient {

    private static final Logger log = LoggerFactory.getLogger(EmailClient.class);

    private final RestTemplate restTemplate;

    public EmailClient() {
        this.restTemplate = new RestTemplate();
    }

    public void sendQuizResultEmail(QuizResultEmailRequest request) {
        try {
            String emailServiceUrl = "http://email-service:8086/api/email/quiz-result";
            restTemplate.postForEntity(emailServiceUrl, request, String.class);
            log.info("Email notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
        }
    }
}
