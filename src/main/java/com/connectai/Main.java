package com.connectai;

import com.connectai.config.AppConfig;
import com.connectai.config.ThemeColors;
import com.connectai.ui.auth.SplashScreen;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * ConnectAI Main Application Entry Point.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize FlatLaf Dark Look and Feel with custom palette properties
        try {
            FlatDarkLaf.setup();
            UIManager.put("Component.accentColor", ThemeColors.PRIMARY_ACCENT);
            UIManager.put("Panel.background", ThemeColors.MAIN_BG);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("Button.arc", 12);
            UIManager.put("ScrollBar.thumbArc", 12);
            UIManager.put("ScrollBar.width", 10);
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme: " + e.getMessage());
        }

        // Load configuration
        AppConfig.load();

        // Launch Splash Screen on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.startInitialization();
        });
    }
}
