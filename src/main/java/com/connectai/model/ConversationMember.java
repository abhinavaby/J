package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationMember {
    @JsonProperty("conversation_id")
    private String conversationId;
    
    @JsonProperty("user_id")
    private String userId;
    
    private Role role;
    
    @JsonProperty("joined_at")
    private String joinedAt;
    
    @JsonProperty("is_muted")
    private boolean isMuted;
    
    @JsonProperty("is_archived")
    private boolean isArchived;
    
    @JsonProperty("is_pinned")
    private boolean isPinned;
    
    @JsonProperty("last_read_message_id")
    private String lastReadMessageId;

    private Profile profile;
    private Conversation conversation;

    public ConversationMember() {}

    public ConversationMember(String conversationId, String userId, Role role, Profile profile) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.role = role;
        this.profile = profile;
    }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getJoinedAt() { return joinedAt; }
    public void setJoinedAt(String joinedAt) { this.joinedAt = joinedAt; }

    public boolean isMuted() { return isMuted; }
    public void setMuted(boolean muted) { isMuted = muted; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }

    public String getLastReadMessageId() { return lastReadMessageId; }
    public void setLastReadMessageId(String lastReadMessageId) { this.lastReadMessageId = lastReadMessageId; }

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }
}
