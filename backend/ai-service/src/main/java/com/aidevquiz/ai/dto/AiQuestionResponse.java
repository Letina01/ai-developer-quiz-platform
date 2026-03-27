package com.aidevquiz.ai.dto;

import java.util.List;

public record AiQuestionResponse(String question, List<String> options, String correctAnswer, String explanation) {
}
