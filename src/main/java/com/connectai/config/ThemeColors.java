package com.connectai.config;

import java.awt.Color;

/**
 * Premium Neon Lime Green & Electric Cyan Theme.
 */
public class ThemeColors {
    // Backgrounds
    public static final Color MAIN_BG          = Color.decode("#090A0F"); // pitch black base
    public static final Color SIDEBAR_BG       = Color.decode("#0F1118"); // dark sidebar
    public static final Color ELEVATED_SURFACE = Color.decode("#161924"); // elevated card background
    public static final Color MESSAGE_AREA     = Color.decode("#0C0E14"); // chat message area

    // Accents
    public static final Color PRIMARY_ACCENT   = Color.decode("#BBEF1F"); // neon lime green/yellow
    public static final Color SECONDARY_ACCENT = Color.decode("#00F0FF"); // electric cyan

    // Message Bubbles
    public static final Color OUTGOING_BUBBLE  = Color.decode("#0B3036"); // deep electric cyan tint
    public static final Color INCOMING_BUBBLE  = Color.decode("#161A26"); // dark obsidian bubble

    // Text
    public static final Color PRIMARY_TEXT   = Color.decode("#EFF8FF"); // soft brilliant white
    public static final Color SECONDARY_TEXT = Color.decode("#94A3B8"); // muted cyan-grey
    public static final Color MUTED_TEXT     = Color.decode("#556677"); // dim grey

    // Status & Semantic
    public static final Color SUCCESS_ONLINE = Color.decode("#BBEF1F"); // neon lime green
    public static final Color WARNING        = Color.decode("#FACC15"); // neon amber
    public static final Color ERROR          = Color.decode("#FF3366"); // neon red/pink
    public static final Color DIVIDER        = Color.decode("#1E2333"); // dark border/divider

    // Interactive States
    public static final Color HOVER_OVERLAY     = new Color(187, 239, 31, 20);  // neon lime overlay
    public static final Color SELECTION_OVERLAY = new Color(0, 240, 255, 35);   // cyan selection overlay
    public static final Color CARD_BORDER       = new Color(187, 239, 31, 50);  // translucent neon lime border
}
