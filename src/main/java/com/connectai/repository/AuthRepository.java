package com.connectai.repository;

import com.connectai.model.User;
import com.connectai.util.HttpClientUtil;
import com.connectai.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AuthRepository {

    public CompletableFuture<User> signUp(String email, String password, String username, String displayName, String avatarPath) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("username", username);
        metadata.put("display_name", displayName);
        if (avatarPath != null) metadata.put("avatar_path", avatarPath);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("data", metadata);

        return HttpClientUtil.postAsync("/auth/v1/signup", JsonUtil.toJson(body), null)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException(extractErrorMessage(response.body(), "Registration failed"));
                    }
                    return parseUserFromAuthJson(response.body());
                });
    }

    public CompletableFuture<User> signIn(String email, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        return HttpClientUtil.postAsync("/auth/v1/token?grant_type=password", JsonUtil.toJson(body), null)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException(extractErrorMessage(response.body(), "Invalid email or password"));
                    }
                    return parseUserFromAuthJson(response.body());
                });
    }

    public CompletableFuture<User> refreshSession(String refreshToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("refresh_token", refreshToken);

        return HttpClientUtil.postAsync("/auth/v1/token?grant_type=refresh_token", JsonUtil.toJson(body), null)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException(extractErrorMessage(response.body(), "Session expired"));
                    }
                    return parseUserFromAuthJson(response.body());
                });
    }

    public CompletableFuture<Boolean> sendPasswordReset(String email) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);

        return HttpClientUtil.postAsync("/auth/v1/recover", JsonUtil.toJson(body), null)
                .thenApply(response -> response.statusCode() < 400);
    }

    private User parseUserFromAuthJson(String json) {
        try {
            JsonNode node = JsonUtil.getMapper().readTree(json);
            String accessToken = node.path("access_token").asText(null);
            String refreshToken = node.path("refresh_token").asText(null);
            long expiresIn = node.path("expires_in").asLong(3600);

            JsonNode userNode = node.has("user") ? node.path("user") : node;
            String id = userNode.path("id").asText(null);
            String email = userNode.path("email").asText(null);

            User user = new User(id, email, accessToken, refreshToken);
            user.setExpiresIn(expiresIn);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse auth response: " + e.getMessage(), e);
        }
    }

    private String extractErrorMessage(String responseBody, String defaultMsg) {
        try {
            JsonNode node = JsonUtil.getMapper().readTree(responseBody);
            if (node.has("error_description")) return node.get("error_description").asText();
            if (node.has("msg")) return node.get("msg").asText();
            if (node.has("message")) return node.get("message").asText();
        } catch (Exception ignored) {}
        return defaultMsg;
    }
}
