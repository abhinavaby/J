package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageReceipt {
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("delivered_at")
    private String deliveredAt;
    
    @JsonProperty("read_at")
    private String readAt;

    public MessageReceipt() {}

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(String deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getReadAt() { return readAt; }
    public void setReadAt(String readAt) { this.readAt = readAt; }
}
