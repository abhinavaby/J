package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PollOption {
    private String id;
    
    @JsonProperty("poll_id")
    private String pollId;
    
    @JsonProperty("option_text")
    private String optionText;
    
    private int position;

    // Transient UI counts
    private int voteCount;
    private double percentage;
    private boolean hasVoted;
    private List<String> userVotes = new ArrayList<>();

    public PollOption() {}

    public PollOption(String id, String pollId, String optionText, int voteCount, double percentage) {
        this.id = id;
        this.pollId = pollId;
        this.optionText = optionText;
        this.voteCount = voteCount;
        this.percentage = percentage;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPollId() { return pollId; }
    public void setPollId(String pollId) { this.pollId = pollId; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public boolean isHasVoted() { return hasVoted; }
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }

    public List<String> getUserVotes() { return userVotes; }
    public void setUserVotes(List<String> userVotes) { this.userVotes = userVotes; }
}
