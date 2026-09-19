package com.connectai.service;

import com.connectai.model.Attachment;
import com.connectai.model.Message;
import com.connectai.model.MessageStatus;
import com.connectai.model.MessageType;
import com.connectai.repository.AttachmentRepository;
import com.connectai.repository.MessageRepository;
import com.connectai.repository.StorageRepository;
import com.connectai.util.FileUtil;
import java.io.File;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AttachmentService {
    private static final AttachmentService instance = new AttachmentService();
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB limit

    private final StorageRepository storageRepository;
    private final AttachmentRepository attachmentRepository;
    private final MessageRepository messageRepository;

    private AttachmentService() {
        this.storageRepository = new StorageRepository();
        this.attachmentRepository = new AttachmentRepository();
        this.messageRepository = new MessageRepository();
    }

    public static AttachmentService getInstance() {
        return instance;
    }

    public CompletableFuture<Message> uploadAndSendFile(String conversationId, File file, String caption, MessageType forcedType, int durationSeconds) {
        if (file == null || !file.exists()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("File does not exist"));
        }
        if (file.length() > MAX_FILE_SIZE) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("File size exceeds 50MB limit"));
        }

        if (com.connectai.config.AppConfig.isMockMode()) {
            String messageId = UUID.randomUUID().toString();
            String mimeType = FileUtil.getMimeType(file);
            MessageType msgType = forcedType != null ? forcedType : (FileUtil.isImage(mimeType) ? MessageType.IMAGE : MessageType.DOCUMENT);

            Message msg = new Message();
            msg.setId(messageId);
            msg.setConversationId(conversationId);
            msg.setSenderId(AuthService.getInstance().getCurrentUser().getId());
            msg.setSenderProfile(AuthService.getInstance().getCurrentProfile());
            msg.setMessageType(msgType);
            msg.setContent(caption != null && !caption.isBlank() ? caption : file.getName());
            msg.setStatus(MessageStatus.SENT);
            msg.setCreatedAt(java.time.Instant.now().toString());

            Attachment att = new Attachment();
            att.setMessageId(messageId);
            att.setStoragePath(file.getAbsolutePath());
            att.setOriginalFilename(file.getName());
            att.setMimeType(mimeType);
            att.setFileSize(file.length());
            att.setDurationSeconds(durationSeconds);
            msg.setAttachment(att);

            com.connectai.mock.MockDataProvider.getInstance().getMessages(conversationId).add(0, msg);
            return CompletableFuture.completedFuture(msg);
        }

        String token = AuthService.getInstance().getAccessToken();
        String userId = AuthService.getInstance().getCurrentUser().getId();
        String mimeType = FileUtil.getMimeType(file);

        MessageType msgType = forcedType;
        if (msgType == null) {
            if (FileUtil.isImage(mimeType)) msgType = MessageType.IMAGE;
            else if (FileUtil.isAudio(mimeType)) msgType = MessageType.AUDIO;
            else if (FileUtil.isVideo(mimeType)) msgType = MessageType.VIDEO;
            else msgType = MessageType.DOCUMENT;
        }

        String messageId = UUID.randomUUID().toString();
        String storagePath = conversationId + "/" + messageId + "_" + file.getName();

        final MessageType finalMsgType = msgType;

        return CompletableFuture.supplyAsync(() -> {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read file bytes: " + e.getMessage(), e);
            }
        }).thenCompose(data -> storageRepository.uploadFile(storagePath, data, mimeType, token))
          .thenCompose(path -> {
              // Create message
              Message msg = new Message();
              msg.setId(messageId);
              msg.setConversationId(conversationId);
              msg.setSenderId(userId);
              msg.setMessageType(finalMsgType);
              msg.setContent(caption != null ? caption : file.getName());
              msg.setStatus(MessageStatus.SENDING);
              msg.setCreatedAt(java.time.Instant.now().toString());

              return messageRepository.sendMessage(msg, token);
          })
          .thenCompose(sentMsg -> {
              // Create attachment record
              Attachment att = new Attachment();
              att.setMessageId(sentMsg.getId());
              att.setStoragePath(storagePath);
              att.setOriginalFilename(file.getName());
              att.setMimeType(mimeType);
              att.setFileSize(file.length());
              att.setDurationSeconds(durationSeconds);

              return attachmentRepository.createAttachmentRecord(att, token)
                      .thenApply(createdAtt -> {
                          sentMsg.setAttachment(createdAtt);
                          sentMsg.setStatus(MessageStatus.SENT);
                          return sentMsg;
                      });
          });
    }

    public CompletableFuture<String> getSignedDownloadUrl(String storagePath) {
        if (com.connectai.config.AppConfig.isMockMode()) {
            return CompletableFuture.completedFuture(storagePath);
        }
        String token = AuthService.getInstance().getAccessToken();
        return storageRepository.getSignedUrl(storagePath, 3600, token);
    }
}
