package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.util.ImageUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class AvatarComponent extends JComponent {
    private int size;
    private String displayName;
    private BufferedImage image;
    private boolean isOnline;
    private boolean showOnlineStatus;

    public AvatarComponent(int size, String displayName, boolean showOnlineStatus) {
        this.size = size;
        this.displayName = displayName;
        this.showOnlineStatus = showOnlineStatus;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        repaint();
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public void setOnline(boolean online) {
        this.isOnline = online;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (image != null) {
            BufferedImage circular = ImageUtil.makeCircularImage(image, size);
            g2.drawImage(circular, 0, 0, null);
        } else {
            // Draw background circle with fallback initials
            g2.setColor(ThemeColors.PRIMARY_ACCENT);
            g2.fillOval(0, 0, size, size);

            g2.setColor(ThemeColors.ACCENT_TEXT);
            g2.setFont(ThemeFonts.TITLE_SMALL);
            String initials = getInitials(displayName);
            int textX = (size - g2.getFontMetrics().stringWidth(initials)) / 2;
            int textY = (size - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(initials, textX, textY);
        }

        // Draw online indicator dot
        if (showOnlineStatus) {
            int dotSize = Math.max(10, size / 4);
            int dotX = size - dotSize - 1;
            int dotY = size - dotSize - 1;

            g2.setColor(ThemeColors.SIDEBAR_BG);
            g2.fillOval(dotX - 2, dotY - 2, dotSize + 4, dotSize + 4);

            g2.setColor(isOnline ? ThemeColors.SUCCESS_ONLINE : ThemeColors.MUTED_TEXT);
            g2.fillOval(dotX, dotY, dotSize, dotSize);
        }

        g2.dispose();
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank())
            return "CA";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}
