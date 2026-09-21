package com.connectai.ui.components;

import com.connectai.config.AppConfig;
import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.service.AuthService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SettingsDialog extends JDialog {

    public SettingsDialog(Frame owner) {
        super(owner, "ConnectAI Settings", true);
        setSize(new Dimension(460, 480));
        setLocationRelativeTo(owner);

        JPanel mainPanel = new RoundedPanel(0, ThemeColors.MAIN_BG);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("⚙ Settings & Preferences");
        title.setFont(ThemeFonts.TITLE_MEDIUM);
        title.setForeground(ThemeColors.PRIMARY_TEXT);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Account section
        String name = AuthService.getInstance().getCurrentProfile() != null
                ? AuthService.getInstance().getCurrentProfile().getEffectiveName()
                : "User";
        String email = AuthService.getInstance().getCurrentUser() != null
                ? AuthService.getInstance().getCurrentUser().getEmail()
                : "N/A";

        addInfoRow(content, "User Account", name + " (" + email + ")");
        addInfoRow(content, "Application Version", AppConfig.getAppName() + " v" + AppConfig.getAppVersion());
        addInfoRow(content, "Supabase Server", AppConfig.getSupabaseUrl());

        JCheckBox soundCheck = new JCheckBox("Play notification sound on new message", true);
        soundCheck.setFont(ThemeFonts.BODY_MEDIUM);
        soundCheck.setForeground(ThemeColors.PRIMARY_TEXT);
        soundCheck.setOpaque(false);

        JCheckBox aiCheck = new JCheckBox("Enable AI features (Catch Me Up & Draft Rephrase)", true);
        aiCheck.setFont(ThemeFonts.BODY_MEDIUM);
        aiCheck.setForeground(ThemeColors.PRIMARY_TEXT);
        aiCheck.setOpaque(false);

        content.add(javax.swing.Box.createVerticalStrut(16));
        content.add(soundCheck);
        content.add(javax.swing.Box.createVerticalStrut(8));
        content.add(aiCheck);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        PremiumButton saveBtn = new PremiumButton("Save & Close", ThemeColors.PRIMARY_ACCENT, ThemeColors.PRIMARY_TEXT);
        saveBtn.addActionListener(e -> dispose());
        footer.add(saveBtn, BorderLayout.EAST);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(content, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addInfoRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel l = new JLabel(label);
        l.setFont(ThemeFonts.BODY_BOLD);
        l.setForeground(ThemeColors.SECONDARY_TEXT);

        JLabel v = new JLabel(value);
        v.setFont(ThemeFonts.BODY_MEDIUM);
        v.setForeground(ThemeColors.PRIMARY_TEXT);

        row.add(l, BorderLayout.NORTH);
        row.add(v, BorderLayout.SOUTH);

        parent.add(row);
    }
}
