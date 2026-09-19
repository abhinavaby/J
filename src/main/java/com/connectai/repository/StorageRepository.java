package com.connectai.repository;

import com.connectai.config.AppConfig;
import com.connectai.util.HttpClientUtil;
import com.connectai.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StorageRepository {

    public CompletableFuture<String> uploadFile(String storagePath, byte[] data, String contentType, String accessToken) {
        String path = "/storage/v1/object/chat-attachments/" + storagePath;
        return HttpClientUtil.postBinaryAsync(path, data, contentType, accessToken)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("Storage upload failed: " + response.body());
                    }
                    return storagePath;
                });
    }

    public CompletableFuture<String> getSignedUrl(String storagePath, int expiresInSeconds, String accessToken) {
        String path = "/storage/v1/object/sign/chat-attachments/" + storagePath;
        Map<String, Object> body = Map.of("expiresIn", expiresInSeconds);

        return HttpClientUtil.postAsync(path, JsonUtil.toJson(body), accessToken)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("Failed to generate signed URL");
                    }
                    try {
                        JsonNode node = JsonUtil.getMapper().readTree(response.body());
                        String signedUrlPath = node.path("signedURL").asText(null);
                        if (signedUrlPath != null) {
                            return AppConfig.getSupabaseUrl() + signedUrlPath;
                        }
                    } catch (Exception ignored) {}
                    return AppConfig.getSupabaseUrl() + "/storage/v1/object/authenticated/chat-attachments/" + storagePath;
                });
    }
}
