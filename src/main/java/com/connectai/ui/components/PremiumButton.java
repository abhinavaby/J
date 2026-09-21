package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeDimensions;
import com.connectai.config.ThemeFonts;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class PremiumButton extends JButton {
    private Color normalBg;
    private Color hoverBg;
    private Color pressedBg;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = ThemeDimensions.CORNER_RADIUS;

    public PremiumButton(String text) {
        this(text, ThemeColors.PRIMARY_ACCENT, Color.WHITE);
    }

    public PremiumButton(String text, Color bg, Color fg) {
        super(text);
        this.normalBg = bg;
        this.hoverBg = bg.brighter();
        this.pressedBg = bg.darker();

        setFont(ThemeFonts.BODY_BOLD);
        setForeground(fg);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = false;
                    repaint();
                }
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isEnabled()) {
            g2.setColor(ThemeColors.MUTED_TEXT);
        } else if (isPressed) {
            g2.setColor(pressedBg);
        } else if (isHovered) {
            g2.setColor(hoverBg);
        } else {
            g2.setColor(normalBg);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose();

        super.paintComponent(g);
    }
}
