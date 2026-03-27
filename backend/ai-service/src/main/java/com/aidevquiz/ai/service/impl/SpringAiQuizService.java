package com.aidevquiz.ai.service.impl;

import com.aidevquiz.ai.dto.AiQuestionResponse;
import com.aidevquiz.ai.dto.GenerateQuizRequest;
import com.aidevquiz.ai.service.AiQuizService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SpringAiQuizService implements AiQuizService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAiQuizService.class);

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public SpringAiQuizService(ChatClient.Builder builder, ObjectMapper objectMapper) {
        this.chatClient = builder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public List<AiQuestionResponse> generateQuestions(GenerateQuizRequest request) {
        String prompt = """
                Generate %d multiple-choice quiz questions for software developers.
                Domain: %s
                Topic: %s
                Difficulty: %s

                Return only a valid JSON array.
                Do not wrap the JSON in markdown code fences.
                Generate exactly 4 answer options for every question.
                Each array item must have:
                question,
                options (array of exactly 4 strings),
                correctAnswer (MUST be one of the strings from the options array, verbatim),
                explanation
                """.formatted(
                request.numberOfQuestions(),
                request.domain(),
                request.topic(),
                request.difficulty()
        );

        String content = chatClient.prompt().user(prompt).call().content();
        String normalizedContent = extractJsonArray(content);

        try {
            return objectMapper.readValue(normalizedContent, new TypeReference<>() {});
        } catch (Exception exception) {
            LOGGER.warn("AI provider returned invalid JSON payload: {}", abbreviate(content));
            throw new IllegalStateException("AI provider returned invalid quiz JSON", exception);
        }
    }

    private String extractJsonArray(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("AI provider returned empty content");
        }

        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```(?:json)?\\s*", "");
            trimmed = trimmed.replaceFirst("\\s*```$", "");
        }

        int start = trimmed.indexOf('[');
        int end = trimmed.lastIndexOf(']');

        if (start < 0 || end < start) {
            throw new IllegalStateException("AI provider response did not contain a JSON array");
        }

        return trimmed.substring(start, end + 1);
    }

    private String abbreviate(String content) {
        String normalized = content == null ? "" : content.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 300 ? normalized : normalized.substring(0, 300) + "...";
    }
}
