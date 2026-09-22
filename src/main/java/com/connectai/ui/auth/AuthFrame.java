package com.connectai.ui.auth;

import com.connectai.config.AppConfig;
import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeDimensions;
import com.connectai.config.ThemeFonts;
import com.connectai.service.AuthService;
import com.connectai.ui.chat.MainChatFrame;
import com.connectai.ui.components.PasswordFieldWithToggle;
import com.connectai.ui.components.PremiumButton;
import com.connectai.ui.components.RoundedPanel;
import com.connectai.ui.components.RoundedTextField;
import com.connectai.ui.components.ToastManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class AuthFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardsPanel;

    // Login components
    private RoundedTextField loginEmailField;
    private PasswordFieldWithToggle loginPasswordField;
    private PremiumButton loginButton;

    // Register components
    private RoundedTextField regNameField;
    private RoundedTextField regUserField;
    private RoundedTextField regEmailField;
    private PasswordFieldWithToggle regPasswordField;
    private PasswordFieldWithToggle regConfirmPasswordField;
    private PremiumButton registerButton;

    public AuthFrame() {
        setTitle(AppConfig.getAppName() + " — Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 680));
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);

        JPanel mainPanel = new RoundedPanel(0, ThemeColors.MAIN_BG);
        mainPanel.setLayout(new BorderLayout());

        // Left Branding Panel
        JPanel leftBrandPanel = new RoundedPanel(0, ThemeColors.SIDEBAR_BG);
        leftBrandPanel.setPreferredSize(new Dimension(420, 680));
        leftBrandPanel.setLayout(new GridBagLayout());

        JPanel brandContent = new JPanel();
        brandContent.setLayout(new BoxLayout(brandContent, BoxLayout.Y_AXIS));
        brandContent.setOpaque(false);
        brandContent.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel logo = new JLabel("⚡ ConnectAI", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 36));
        logo.setForeground(ThemeColors.PRIMARY_ACCENT);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><center>AI-Powered Realtime Communication Platform</center></html>",
                SwingConstants.CENTER);
        tagline.setFont(ThemeFonts.BODY_LARGE);
        tagline.setForeground(ThemeColors.SECONDARY_TEXT);
        tagline.setAlignmentX(CENTER_ALIGNMENT);

        JLabel desc = new JLabel(
                "<html><center>Connect instantly, summarize discussions, rephrase messages, and collaborate in realtime with modern AI intelligence.</center></html>",
                SwingConstants.CENTER);
        desc.setFont(ThemeFonts.BODY_MEDIUM);
        desc.setForeground(ThemeColors.MUTED_TEXT);
        desc.setAlignmentX(CENTER_ALIGNMENT);

        brandContent.add(logo);
        brandContent.add(javax.swing.Box.createVerticalStrut(16));
        brandContent.add(tagline);
        brandContent.add(javax.swing.Box.createVerticalStrut(24));
        brandContent.add(desc);

        leftBrandPanel.add(brandContent);

        // Right Cards Panel (Login / Register)
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setOpaque(false);

        cardsPanel.add(createLoginCard(), "LOGIN");
        cardsPanel.add(createRegisterCard(), "REGISTER");

        mainPanel.add(leftBrandPanel, BorderLayout.WEST);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createLoginCard() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        JPanel card = new RoundedPanel(16, ThemeColors.ELEVATED_SURFACE, ThemeColors.CARD_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(440, 520));
        card.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));

        JLabel h1 = new JLabel("Welcome back");
        h1.setFont(ThemeFonts.TITLE_LARGE);
        h1.setForeground(ThemeColors.PRIMARY_TEXT);

        JLabel sub = new JLabel("Log in to your ConnectAI account");
        sub.setFont(ThemeFonts.BODY_MEDIUM);
        sub.setForeground(ThemeColors.MUTED_TEXT);

        loginEmailField = new RoundedTextField("Email address");
        loginPasswordField = new PasswordFieldWithToggle("Password");

        JLabel forgotLabel = new JLabel("Forgot password?");
        forgotLabel.setFont(ThemeFonts.BODY_SMALL);
        forgotLabel.setForeground(ThemeColors.PRIMARY_ACCENT);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ForgotPasswordDialog(AuthFrame.this).setVisible(true);
            }
        });

        loginButton = new PremiumButton("Log In", ThemeColors.PRIMARY_ACCENT, ThemeColors.ACCENT_TEXT);
        loginButton.setPreferredSize(new Dimension(368, 44));
        loginButton.addActionListener(e -> performLogin());

        PremiumButton mockModeButton = new PremiumButton("⚡ Explore UI with Mock Data", ThemeColors.ELEVATED_SURFACE,
                ThemeColors.PRIMARY_TEXT);
        mockModeButton.setPreferredSize(new Dimension(368, 40));
        mockModeButton.setFont(ThemeFonts.BODY_BOLD);
        mockModeButton.addActionListener(e -> {
            AppConfig.setMockMode(true);
            dispose();
            SwingUtilities.invokeLater(() -> new MainChatFrame().setVisible(true));
        });

        JLabel switchRegister = new JLabel("Don't have an account? Sign up");
        switchRegister.setFont(ThemeFonts.BODY_MEDIUM);
        switchRegister.setForeground(ThemeColors.SECONDARY_ACCENT);
        switchRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(cardsPanel, "REGISTER");
            }
        });

        card.add(h1);
        card.add(javax.swing.Box.createVerticalStrut(4));
        card.add(sub);
        card.add(javax.swing.Box.createVerticalStrut(24));
        card.add(loginEmailField);
        card.add(javax.swing.Box.createVerticalStrut(12));
        card.add(loginPasswordField);
        card.add(javax.swing.Box.createVerticalStrut(6));
        card.add(forgotLabel);
        card.add(javax.swing.Box.createVerticalStrut(18));
        card.add(loginButton);
        card.add(javax.swing.Box.createVerticalStrut(10));
        card.add(mockModeButton);
        card.add(javax.swing.Box.createVerticalStrut(14));
        card.add(switchRegister);

        container.add(card);
        return container;
    }

    private JPanel createRegisterCard() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        JPanel card = new RoundedPanel(16, ThemeColors.ELEVATED_SURFACE, ThemeColors.CARD_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(440, 580));
        card.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel h1 = new JLabel("Create an Account");
        h1.setFont(ThemeFonts.TITLE_LARGE);
        h1.setForeground(ThemeColors.PRIMARY_TEXT);

        JLabel sub = new JLabel("Join ConnectAI today");
        sub.setFont(ThemeFonts.BODY_MEDIUM);
        sub.setForeground(ThemeColors.MUTED_TEXT);

        regNameField = new RoundedTextField("Display Name (e.g. John Doe)");
        regUserField = new RoundedTextField("Username (unique)");
        regEmailField = new RoundedTextField("Email address");
        regPasswordField = new PasswordFieldWithToggle("Password");
        regConfirmPasswordField = new PasswordFieldWithToggle("Confirm Password");

        registerButton = new PremiumButton("Create Account", ThemeColors.PRIMARY_ACCENT, ThemeColors.ACCENT_TEXT);
        registerButton.setPreferredSize(new Dimension(368, 44));
        registerButton.addActionListener(e -> performRegistration());

        JLabel switchLogin = new JLabel("Already have an account? Log in");
        switchLogin.setFont(ThemeFonts.BODY_MEDIUM);
        switchLogin.setForeground(ThemeColors.SECONDARY_ACCENT);
        switchLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(cardsPanel, "LOGIN");
            }
        });

        card.add(h1);
        card.add(javax.swing.Box.createVerticalStrut(4));
        card.add(sub);
        card.add(javax.swing.Box.createVerticalStrut(20));
        card.add(regNameField);
        card.add(javax.swing.Box.createVerticalStrut(10));
        card.add(regUserField);
        card.add(javax.swing.Box.createVerticalStrut(10));
        card.add(regEmailField);
        card.add(javax.swing.Box.createVerticalStrut(10));
        card.add(regPasswordField);
        card.add(javax.swing.Box.createVerticalStrut(10));
        card.add(regConfirmPasswordField);
        card.add(javax.swing.Box.createVerticalStrut(20));
        card.add(registerButton);
        card.add(javax.swing.Box.createVerticalStrut(16));
        card.add(switchLogin);

        container.add(card);
        return container;
    }

    private void performLogin() {
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getPasswordString();

        if (email.isBlank() || password.isBlank()) {
            ToastManager.showToast(this, "Please fill in all fields", ToastManager.ToastType.WARNING);
            return;
        }

        loginButton.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                AuthService.getInstance().signIn(email, password).get();
                return null;
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                try {
                    get();
                    ToastManager.showToast(AuthFrame.this, "Welcome back!", ToastManager.ToastType.SUCCESS);
                    dispose();
                    SwingUtilities.invokeLater(() -> new MainChatFrame().setVisible(true));
                } catch (Exception ex) {
                    ToastManager.showToast(AuthFrame.this, "Login failed: " + ex.getCause().getMessage(),
                            ToastManager.ToastType.ERROR);
                }
            }
        }.execute();
    }

    private void performRegistration() {
        String displayName = regNameField.getText().trim();
        String username = regUserField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getPasswordString();
        String confirm = regConfirmPasswordField.getPasswordString();

        if (displayName.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            ToastManager.showToast(this, "Please fill in all required fields", ToastManager.ToastType.WARNING);
            return;
        }

        if (!password.equals(confirm)) {
            ToastManager.showToast(this, "Passwords do not match", ToastManager.ToastType.ERROR);
            return;
        }

        registerButton.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                AuthService.getInstance().signUp(email, password, username, displayName, null).get();
                return null;
            }

            @Override
            protected void done() {
                registerButton.setEnabled(true);
                try {
                    get();
                    ToastManager.showToast(AuthFrame.this, "Account created successfully!",
                            ToastManager.ToastType.SUCCESS);
                    dispose();
                    SwingUtilities.invokeLater(() -> new MainChatFrame().setVisible(true));
                } catch (Exception ex) {
                    ToastManager.showToast(AuthFrame.this, "Sign up failed: " + ex.getCause().getMessage(),
                            ToastManager.ToastType.ERROR);
                }
            }
        }.execute();
    }
}
