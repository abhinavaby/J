package com.connectai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AIRephraseResponse {
    private String originalText;
    private String rephrasedText;
    private List<String> alternatives;

    public AIRephraseResponse() {}

    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }

    public String getRephrasedText() { return rephrasedText; }
    public void setRephrasedText(String rephrasedText) { this.rephrasedText = rephrasedText; }

    public List<String> getAlternatives() { return alternatives; }
    public void setAlternatives(List<String> alternatives) { this.alternatives = alternatives; }
}
