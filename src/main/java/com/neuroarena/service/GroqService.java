package com.neuroarena.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private static final Logger log = LoggerFactory.getLogger(GroqService.class);
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String analyzePersonality(Map<String, Object> resultData) {
        try {
            Map<String, Object> body = Map.of(
                    "model",       "llama-3.1-8b-instant",
                    "stream",      false,
                    "max_tokens",  300,
                    "temperature", 0.85,
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "You are a neural combat analyst for NeuroArena. Give a short " +
                                            "3-4 sentence psychological profile. Use dramatic sci-fi cyberpunk " +
                                            "language. Be specific about the player's actual numbers. " +
                                            "Plain text only — no markdown, no bullet points, no preamble."
                            ),
                            Map.of("role", "user", "content", buildPrompt(resultData))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GROQ_URL, new HttpEntity<>(body, headers), Map.class
            );

            List<?> choices = (List<?>) response.getBody().get("choices");
            Map<?, ?> message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
            return message.get("content").toString().trim();

        } catch (HttpClientErrorException e) {
            log.warn("Groq API error {} — falling back", e.getStatusCode());
            return null;
        } catch (Exception e) {
            log.warn("Groq unavailable: {} — falling back", e.getMessage());
            return null;
        }
    }

    private String buildPrompt(Map<String, Object> data) {
        int     finalRank      = ((Number) data.get("finalRank")).intValue();
        int     totalPlayers   = ((Number) data.get("totalPlayers")).intValue();
        int     score          = ((Number) data.get("score")).intValue();
        double  accuracy       = ((Number) data.get("accuracy")).doubleValue();
        double  avgSpeedSecs   = ((Number) data.get("avgSpeedSecs")).doubleValue();
        int     correctAnswers = ((Number) data.get("correctAnswers")).intValue();
        int     totalQuestions = ((Number) data.get("totalQuestions")).intValue();
        boolean userWon        = (boolean) data.get("userWon");

        List<?> attrs = (List<?>) data.get("cognitiveAttributes");
        StringBuilder attrStr = new StringBuilder();
        for (Object a : attrs) {
            Map<?, ?> attr = (Map<?, ?>) a;
            attrStr.append(attr.get("name")).append(": ")
                    .append(attr.get("value")).append("/100  ");
        }

        return String.format("""
            Analyze this NeuroArena battle and write a personalized psychological \
            combat profile in 3-4 sentences. Reference specific numbers.
            Make the vocabulary easy to understand to a normal person also.

            - Rank     : %d / %d players
            - Score    : %d pts
            - Accuracy : %.1f%%
            - Avg Speed: %.1fs per answer
            - Correct  : %d / %d questions
            - Victory  : %s
            - Cognitive: %s
            """,
                finalRank, totalPlayers, score, accuracy,
                avgSpeedSecs, correctAnswers, totalQuestions,
                userWon ? "YES" : "NO", attrStr
        );
    }
}