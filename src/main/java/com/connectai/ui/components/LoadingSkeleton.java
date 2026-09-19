package com.connectai.ui.components;

import com.connectai.config.ThemeColors;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.Timer;

public class LoadingSkeleton extends JPanel {
    private float alpha = 0.2f;
    private boolean increasing = true;
    private Timer timer;

    public LoadingSkeleton() {
        setOpaque(false);
        timer = new Timer(50, e -> {
            if (increasing) {
                alpha += 0.03f;
                if (alpha >= 0.6f) increasing = false;
            } else {
                alpha -= 0.03f;
                if (alpha <= 0.15f) increasing = true;
            }
            repaint();
        });
        timer.start();
    }

    public void stopAnimation() {
        if (timer != null) timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(ThemeColors.ELEVATED_SURFACE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        g2.setColor(new java.awt.Color(255, 255, 255, (int) (alpha * 255)));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
    }
}
