package com.connectai.service;

import com.connectai.model.AIRephraseResponse;
import com.connectai.model.AISummaryResponse;
import com.connectai.model.Message;
import com.connectai.model.Tone;
import com.connectai.util.HttpClientUtil;
import com.connectai.util.JsonUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AIService {
    private static final AIService instance = new AIService();

    private AIService() {}

    public static AIService getInstance() {
        return instance;
    }

    public CompletableFuture<AISummaryResponse> summarizeConversation(String conversationId, List<Message> messages) {
        if (com.connectai.config.AppConfig.isMockMode()) {
            return CompletableFuture.completedFuture(com.connectai.mock.MockDataProvider.getInstance().summarize(conversationId));
        }
        String token = AuthService.getInstance().getAccessToken();
        if (token == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("User not authenticated"));
        }

        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            if (m.getContent() != null && !m.getContent().isBlank() && !m.isDeleted()) {
                String senderName = m.getSenderProfile() != null ? m.getSenderProfile().getEffectiveName() : "User";
                sb.append(senderName).append(": ").append(m.getContent()).append("\n");
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("action", "summarize");
        body.put("conversationId", conversationId);
        body.put("messagesText", sb.toString());

        return HttpClientUtil.postAsync("/functions/v1/ai-assistant", JsonUtil.toJson(body), token)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("AI Summary failed: " + response.body());
                    }
                    return JsonUtil.fromJson(response.body(), AISummaryResponse.class);
                });
    }

    public CompletableFuture<AIRephraseResponse> rephraseMessage(String text, Tone tone) {
        if (com.connectai.config.AppConfig.isMockMode()) {
            return CompletableFuture.completedFuture(com.connectai.mock.MockDataProvider.getInstance().rephrase(text, tone));
        }
        String token = AuthService.getInstance().getAccessToken();
        if (token == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("User not authenticated"));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("action", "rephrase");
        body.put("text", text);
        body.put("tone", tone != null ? tone.getDisplayName() : "Professional");

        return HttpClientUtil.postAsync("/functions/v1/ai-assistant", JsonUtil.toJson(body), token)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("AI Rephrase failed: " + response.body());
                    }
                    return JsonUtil.fromJson(response.body(), AIRephraseResponse.class);
                });
    }
}
