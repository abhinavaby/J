package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Conversation {
    private String id;
    private ConversationType type;
    private String name;
    private String description;
    
    @JsonProperty("image_path")
    private String imagePath;
    
    @JsonProperty("created_by")
    private String createdBy;
    
    @JsonProperty("invite_code")
    private String inviteCode;
    
    @JsonProperty("is_private")
    private boolean isPrivate;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;

    // Transient UI helper properties
    private Message latestMessage;
    private int unreadCount;
    private boolean isMuted;
    private boolean isArchived;
    private boolean isPinned;
    private String draft;
    private Profile directPartnerProfile;
    private List<ConversationMember> members;

    public Conversation() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ConversationType getType() { return type; }
    public void setType(ConversationType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean privateStatus) { isPrivate = privateStatus; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Message getLatestMessage() { return latestMessage; }
    public void setLatestMessage(Message latestMessage) { this.latestMessage = latestMessage; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public boolean isMuted() { return isMuted; }
    public void setMuted(boolean muted) { isMuted = muted; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }

    public String getDraft() { return draft; }
    public void setDraft(String draft) { this.draft = draft; }

    public Profile getDirectPartnerProfile() { return directPartnerProfile; }
    public void setDirectPartnerProfile(Profile directPartnerProfile) { this.directPartnerProfile = directPartnerProfile; }

    public List<ConversationMember> getMembers() { return members; }
    public void setMembers(List<ConversationMember> members) { this.members = members; }

    public String getDisplayTitle() {
        if (type == ConversationType.DIRECT && directPartnerProfile != null) {
            return directPartnerProfile.getEffectiveName();
        }
        return name != null && !name.isBlank() ? name : "Group Chat";
    }
}
