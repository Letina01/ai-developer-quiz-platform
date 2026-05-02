package com.aidevquiz.email.service;

import com.aidevquiz.email.dto.QuizResultEmailRequest;
import com.aidevquiz.email.dto.WelcomeEmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username:}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendQuizResultEmail(QuizResultEmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(request.to());
        message.setSubject("Quiz Result: " + request.quizTitle());
        message.setText(buildQuizResultEmailContent(request));

        try {
            mailSender.send(message);
            log.info("Email sent successfully to: {}", request.to());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }

    public void sendOtpEmail(OtpEmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(request.to());
        message.setSubject("Password Reset OTP - AI Developer Quiz");
        message.setText(buildOtpEmailContent(request));

        try {
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", request.to());
        } catch (Exception e) {
            log.error("Failed to send OTP email: {}", e.getMessage());
        }
    }

    public void sendWelcomeEmail(WelcomeEmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(request.to());
        message.setSubject("Welcome to AI Developer Quiz Platform");
        message.setText(buildWelcomeEmailContent(request));

        try {
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", request.to());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }

    private String buildQuizResultEmailContent(QuizResultEmailRequest request) {
        return String.format("""
            Dear %s,
            
            Congratulations on completing the quiz: %s!
            
            Your Results:
            --------------
            Total Questions: %d
            Correct Answers: %d
            Score: %.2f%%
            
            %s
            
            Keep practicing and improving!
            
            Best regards,
            AI Developer Quiz Platform Team
            """,
            request.userName(),
            request.quizTitle(),
            request.totalQuestions(),
            request.correctAnswers(),
            request.scorePercentage(),
            request.recommendations() != null ? "\nRecommendations:\n" + request.recommendations() : ""
        );
    }

    private String buildOtpEmailContent(OtpEmailRequest request) {
        return String.format("""
            Dear %s,
            
            Your password reset OTP is: %s
            
            This OTP is valid for 5 minutes.
            
            If you didn't request this, please ignore this email.
            
            Best regards,
            AI Developer Quiz Platform Team
            """,
            request.userName(),
            request.otp()
        );
    }

    private String buildWelcomeEmailContent(WelcomeEmailRequest request) {
        String name = request.userName() == null || request.userName().isBlank() ? "User" : request.userName();
        return String.format("""
            Dear %s,

            Welcome to AI Developer Quiz Platform.
            Your account has been created successfully.

            You can now login and start generating quizzes based on your preparation goals.

            Best regards,
            AI Developer Quiz Platform Team
            """,
            name
        );
    }

    public record OtpEmailRequest(
            String to,
            String userName,
            String otp
    ) {}
}
