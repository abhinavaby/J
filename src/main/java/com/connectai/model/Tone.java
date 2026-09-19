package com.connectai.model;

public enum Tone {
    FORMAL("Formal"),
    FRIENDLY("Friendly"),
    CASUAL("Casual"),
    PROFESSIONAL("Professional"),
    SHORTER("Shorter"),
    CONCISE("Concise"),
    ENTHUSIASTIC("Enthusiastic"),
    GRAMMAR_FIX("Grammar Fix"),
    POLITE("Polite"),
    SIMPLIFY("Simplify");

    private final String displayName;

    Tone(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
