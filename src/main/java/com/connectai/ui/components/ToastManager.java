package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ToastManager {
    public enum ToastType {
        SUCCESS(ThemeColors.SUCCESS_ONLINE, "✓"),
        ERROR(ThemeColors.ERROR, "✕"),
        WARNING(ThemeColors.WARNING, "⚠"),
        INFO(ThemeColors.SECONDARY_ACCENT, "ℹ");

        final Color color;
        final String icon;

        ToastType(Color color, String icon) {
            this.color = color;
            this.icon = icon;
        }
    }

    public static void showToast(Component parent, String message, ToastType type) {
        if (parent == null) return;

        JFrame frameCandidate = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(parent);
        if (frameCandidate == null && parent instanceof JFrame) frameCandidate = (JFrame) parent;
        if (frameCandidate == null) return;
        final JFrame frame = frameCandidate;

        RoundedPanel toastPanel = new RoundedPanel(12, ThemeColors.ELEVATED_SURFACE, type.color);
        toastPanel.setLayout(new BorderLayout(10, 0));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel iconLabel = new JLabel(type.icon);
        iconLabel.setFont(ThemeFonts.TITLE_MEDIUM);
        iconLabel.setForeground(type.color);

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(ThemeFonts.BODY_MEDIUM);
        msgLabel.setForeground(ThemeColors.PRIMARY_TEXT);

        toastPanel.add(iconLabel, BorderLayout.WEST);
        toastPanel.add(msgLabel, BorderLayout.CENTER);

        toastPanel.setSize(new Dimension(320, 48));
        int x = frame.getWidth() - 340;
        int y = 40;
        toastPanel.setLocation(x, y);

        frame.getLayeredPane().add(toastPanel, javax.swing.JLayeredPane.POPUP_LAYER);
        frame.getLayeredPane().repaint();

        new javax.swing.Timer(3000, e -> {
            frame.getLayeredPane().remove(toastPanel);
            frame.getLayeredPane().repaint();
            ((javax.swing.Timer) e.getSource()).stop();
        }).start();
    }
}
