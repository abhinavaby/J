package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AISummaryResponse {
    private String summary;
    private List<String> bulletPoints;
    private List<String> keyDecisions;
    private List<String> actionItems;
    private List<String> deadlines;
    private List<String> unansweredQuestions;

    public AISummaryResponse() {}

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getBulletPoints() { return bulletPoints; }
    public void setBulletPoints(List<String> bulletPoints) { this.bulletPoints = bulletPoints; }

    public List<String> getKeyDecisions() { return keyDecisions; }
    public void setKeyDecisions(List<String> keyDecisions) { this.keyDecisions = keyDecisions; }

    public List<String> getActionItems() { return actionItems; }
    public void setActionItems(List<String> actionItems) { this.actionItems = actionItems; }

    public List<String> getDeadlines() { return deadlines; }
    public void setDeadlines(List<String> deadlines) { this.deadlines = deadlines; }

    public List<String> getUnansweredQuestions() { return unansweredQuestions; }
    public void setUnansweredQuestions(List<String> unansweredQuestions) { this.unansweredQuestions = unansweredQuestions; }
}
