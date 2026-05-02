package com.aidevquiz.email.controller;

import com.aidevquiz.email.dto.QuizResultEmailRequest;
import com.aidevquiz.email.dto.WelcomeEmailRequest;
import com.aidevquiz.email.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/quiz-result")
    public ResponseEntity<String> sendQuizResultEmail(@RequestBody QuizResultEmailRequest request) {
        emailService.sendQuizResultEmail(request);
        return ResponseEntity.ok("Email sent successfully");
    }

    @PostMapping("/otp")
    public ResponseEntity<String> sendOtpEmail(@RequestBody EmailService.OtpEmailRequest request) {
        emailService.sendOtpEmail(request);
        return ResponseEntity.ok("OTP email sent successfully");
    }

    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcomeEmail(@RequestBody WelcomeEmailRequest request) {
        emailService.sendWelcomeEmail(request);
        return ResponseEntity.ok("Welcome email sent successfully");
    }
}
