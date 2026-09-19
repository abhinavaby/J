package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class EmptyStatePanel extends JPanel {
    public EmptyStatePanel(String iconSymbol, String title, String description) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JLabel iconLabel = new JLabel(iconSymbol, SwingConstants.CENTER);
        iconLabel.setFont(ThemeFonts.TITLE_LARGE.deriveFont(48.0f));
        iconLabel.setForeground(ThemeColors.PRIMARY_ACCENT);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(ThemeFonts.TITLE_MEDIUM);
        titleLabel.setForeground(ThemeColors.PRIMARY_TEXT);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", SwingConstants.CENTER);
        descLabel.setFont(ThemeFonts.BODY_MEDIUM);
        descLabel.setForeground(ThemeColors.MUTED_TEXT);
        descLabel.setAlignmentX(CENTER_ALIGNMENT);

        content.add(iconLabel);
        content.add(javax.swing.Box.createVerticalStrut(16));
        content.add(titleLabel);
        content.add(javax.swing.Box.createVerticalStrut(8));
        content.add(descLabel);

        add(content, BorderLayout.CENTER);
    }
}
