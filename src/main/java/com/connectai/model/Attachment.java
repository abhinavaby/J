package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Attachment {
    private String id;
    
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("storage_path")
    private String storagePath;
    
    @JsonProperty("original_filename")
    private String originalFilename;
    
    @JsonProperty("mime_type")
    private String mimeType;
    
    @JsonProperty("file_size")
    private long fileSize;
    
    @JsonProperty("duration_seconds")
    private int durationSeconds;
    
    @JsonProperty("created_at")
    private String createdAt;

    private String signedUrl;

    public Attachment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getSignedUrl() { return signedUrl; }
    public void setSignedUrl(String signedUrl) { this.signedUrl = signedUrl; }

    public String getFormattedSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
    }
}
