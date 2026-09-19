package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PollVote {
    private String id;
    
    @JsonProperty("poll_id")
    private String pollId;
    
    @JsonProperty("option_id")
    private String optionId;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("created_at")
    private String createdAt;

    public PollVote() {}

    public PollVote(String pollId, String optionId, String userId) {
        this.pollId = pollId;
        this.optionId = optionId;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPollId() { return pollId; }
    public void setPollId(String pollId) { this.pollId = pollId; }

    public String getOptionId() { return optionId; }
    public void setOptionId(String optionId) { this.optionId = optionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
