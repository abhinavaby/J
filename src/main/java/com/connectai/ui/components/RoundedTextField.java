package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeDimensions;
import com.connectai.config.ThemeFonts;
import com.connectai.config.ThemeSpacing;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class RoundedTextField extends JTextField implements FocusListener {
    private String placeholder;
    private boolean isFocused = false;
    private Color bg = ThemeColors.ELEVATED_SURFACE;
    private Color border = ThemeColors.CARD_BORDER;
    private Color focusBorder = ThemeColors.PRIMARY_ACCENT;

    public RoundedTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(ThemeFonts.BODY_MEDIUM);
        setForeground(ThemeColors.PRIMARY_TEXT);
        setCaretColor(ThemeColors.PRIMARY_ACCENT);
        setBackground(bg);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(ThemeSpacing.S, ThemeSpacing.M, ThemeSpacing.S, ThemeSpacing.M));
        addFocusListener(this);
    }

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; repaint(); }

    @Override
    public void focusGained(FocusEvent e) {
        isFocused = true;
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ThemeDimensions.CORNER_RADIUS, ThemeDimensions.CORNER_RADIUS);

        // Draw border
        g2.setColor(isFocused ? focusBorder : border);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ThemeDimensions.CORNER_RADIUS, ThemeDimensions.CORNER_RADIUS);
        g2.dispose();

        super.paintComponent(g);

        // Draw placeholder text if empty
        if (getText().isEmpty() && !isFocused && placeholder != null) {
            Graphics2D gPlaceholder = (Graphics2D) g.create();
            gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gPlaceholder.setColor(ThemeColors.MUTED_TEXT);
            gPlaceholder.setFont(getFont());
            int padding = getInsets().left;
            int textY = (getHeight() - gPlaceholder.getFontMetrics().getHeight()) / 2 + gPlaceholder.getFontMetrics().getAscent();
            gPlaceholder.drawString(placeholder, padding, textY);
            gPlaceholder.dispose();
        }
    }
}
