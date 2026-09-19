package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.model.AISummaryResponse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class AISummaryDialog extends JDialog {

    public AISummaryDialog(Frame owner, AISummaryResponse summary) {
        super(owner, "Catch Me Up — AI Summary", true);
        setSize(new Dimension(540, 600));
        setLocationRelativeTo(owner);

        JPanel mainPanel = new RoundedPanel(0, ThemeColors.MAIN_BG);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("✨ AI Group Chat Summary");
        titleLabel.setFont(ThemeFonts.TITLE_MEDIUM);
        titleLabel.setForeground(ThemeColors.PRIMARY_ACCENT);

        JLabel subLabel = new JLabel("AI-generated summary of recent conversation");
        subLabel.setFont(ThemeFonts.BODY_SMALL);
        subLabel.setForeground(ThemeColors.MUTED_TEXT);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);

        // Content
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        if (summary != null) {
            addSection(bodyPanel, "📝 Overview", summary.getSummary());
            addListSection(bodyPanel, "🎯 Key Decisions", summary.getKeyDecisions());
            addListSection(bodyPanel, "✅ Action Items", summary.getActionItems());
            addListSection(bodyPanel, "⏰ Deadlines", summary.getDeadlines());
            addListSection(bodyPanel, "❓ Unanswered Questions", summary.getUnansweredQuestions());
        }

        JScrollPane scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        PremiumButton closeBtn = new PremiumButton("Done", ThemeColors.ELEVATED_SURFACE, ThemeColors.PRIMARY_TEXT);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addSection(JPanel parent, String title, String text) {
        if (text == null || text.isBlank()) return;
        JPanel section = new RoundedPanel(10, ThemeColors.ELEVATED_SURFACE);
        section.setLayout(new BorderLayout());
        section.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel h = new JLabel(title);
        h.setFont(ThemeFonts.BODY_BOLD);
        h.setForeground(ThemeColors.SECONDARY_ACCENT);

        JLabel b = new JLabel("<html><body style='width: 420px;'>" + text + "</body></html>");
        b.setFont(ThemeFonts.BODY_MEDIUM);
        b.setForeground(ThemeColors.PRIMARY_TEXT);

        section.add(h, BorderLayout.NORTH);
        section.add(b, BorderLayout.CENTER);

        parent.add(section);
        parent.add(javax.swing.Box.createVerticalStrut(12));
    }

    private void addListSection(JPanel parent, String title, java.util.List<String> items) {
        if (items == null || items.isEmpty()) return;
        StringBuilder sb = new StringBuilder("<ul>");
        for (String item : items) {
            sb.append("<li>").append(item).append("</li>");
        }
        sb.append("</ul>");
        addSection(parent, title, sb.toString());
    }
}
