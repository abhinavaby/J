package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class IconButton extends JButton {
    private boolean isHovered = false;
    private Color hoverColor = ThemeColors.HOVER_OVERLAY;
    private Color iconColor = ThemeColors.SECONDARY_TEXT;

    public IconButton(String iconSymbol, String tooltip) {
        super(iconSymbol);
        setToolTipText(tooltip);
        setFont(ThemeFonts.BODY_BOLD);
        setForeground(iconColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setPreferredSize(new Dimension(36, 36));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setForeground(ThemeColors.PRIMARY_TEXT);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setForeground(iconColor);
                repaint();
            }
        });
    }

    public void setIconColor(Color color) {
        this.iconColor = color;
        setForeground(color);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isHovered) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hoverColor);
            g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
