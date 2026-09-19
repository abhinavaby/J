package com.connectai.repository;

import com.connectai.model.Attachment;
import com.connectai.util.HttpClientUtil;
import com.connectai.util.JsonUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AttachmentRepository {

    public CompletableFuture<Attachment> createAttachmentRecord(Attachment attachment, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("message_id", attachment.getMessageId());
        body.put("storage_path", attachment.getStoragePath());
        body.put("original_filename", attachment.getOriginalFilename());
        body.put("mime_type", attachment.getMimeType());
        body.put("file_size", attachment.getFileSize());
        body.put("duration_seconds", attachment.getDurationSeconds());

        String path = "/rest/v1/attachments?select=*";
        return HttpClientUtil.postAsync(path, JsonUtil.toJson(body), accessToken)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("Failed to save attachment metadata: " + response.body());
                    }
                    List<Attachment> list = JsonUtil.fromJsonList(response.body(), Attachment.class);
                    return list.get(0);
                });
    }

    public CompletableFuture<List<Attachment>> getConversationSharedMedia(String conversationId, String accessToken) {
        String path = "/rest/v1/attachments?select=*,message:messages!inner(conversation_id)&message.conversation_id=eq." + conversationId;
        return HttpClientUtil.getAsync(path, accessToken)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        return List.of();
                    }
                    return JsonUtil.fromJsonList(response.body(), Attachment.class);
                });
    }
}
