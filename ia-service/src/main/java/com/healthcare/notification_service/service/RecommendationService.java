package com.healthcare.notification_service.service;

import com.healthcare.notification_service.model.RecommendationRequest;
import com.healthcare.notification_service.model.RecommendationResponse;
import dev.toonformat.jtoon.JToon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final ChatClient chatClient;
    private static final String PROMPT_TEMPLATE = """
                You are an AI assistant specialized in health insurance plan recommendations.

                You will receive in TOON format (Token Oriented Object Notation):
                1) A member profile (age, medical history, preferences)
                2) A list of available insurance plans

                Your task:
                - Analyze the member profile and preferences
                - Compare them against the available plans
                - Select the SINGLE most suitable plan for the member

                Rules:
                - Return ONLY the plan name
                - Do NOT include explanations, reasoning, or additional text
                - Do NOT include quotes
                - If multiple plans are equally suitable, choose the one with broader coverage
                - If no plan is suitable, return: NO_RECOMMENDED_PLAN

                Input:
                %s
            """;

    public RecommendationResponse classify(RecommendationRequest recommendationRequest) {
        String prompt = String.format(PROMPT_TEMPLATE, JToon.encode(recommendationRequest));

        log.info("RecommendationRequest: {}", recommendationRequest.toString());
        log.info("Prompt a enviar: {}", prompt);
        String response = chatClient
                .prompt(prompt)
                .call()
                .content();

        return new RecommendationResponse(response);
    }

}
