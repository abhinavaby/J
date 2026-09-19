package com.connectai.ui.components;

import com.connectai.config.ThemeDimensions;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;

    public RoundedPanel() {
        this(ThemeDimensions.CORNER_RADIUS, null, null);
    }

    public RoundedPanel(int radius) {
        this(radius, null, null);
    }

    public RoundedPanel(int radius, Color bg) {
        this(radius, bg, null);
    }

    public RoundedPanel(int radius, Color bg, Color border) {
        this.cornerRadius = radius;
        this.backgroundColor = bg;
        this.borderColor = border;
        setOpaque(false);
    }

    public void setBackgroundColor(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.borderColor = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = backgroundColor != null ? backgroundColor : getBackground();
        if (fill != null) {
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
