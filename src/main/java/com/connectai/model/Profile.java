package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
    private String id;
    private String username;
    
    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("avatar_path")
    private String avatarPath;
    
    private String about;
    
    @JsonProperty("last_seen")
    private String lastSeen;
    
    @JsonProperty("created_at")
    private String createdAt;

    private boolean isOnline;

    public Profile() {}

    public Profile(String id, String username, String displayName, String avatarPath) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.avatarPath = avatarPath;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getLastSeen() { return lastSeen; }
    public void setLastSeen(String lastSeen) { this.lastSeen = lastSeen; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public String getEffectiveName() {
        if (displayName != null && !displayName.isBlank()) return displayName;
        if (username != null && !username.isBlank()) return username;
        return "User";
    }
}
