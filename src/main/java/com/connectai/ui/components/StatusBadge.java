package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

public class StatusBadge extends JLabel {
    private Color badgeColor = ThemeColors.SUCCESS_ONLINE;

    public StatusBadge() {
        this("Connected", ThemeColors.SUCCESS_ONLINE);
    }

    public StatusBadge(String text, Color color) {
        super(" ● " + text);
        this.badgeColor = color;
        setFont(ThemeFonts.BADGE);
        setForeground(color);
    }

    public void setStatus(String status, Color color) {
        this.badgeColor = color;
        setText(" ● " + status);
        setForeground(color);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
        g2.dispose();
    }
}
