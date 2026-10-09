package com.connectai.ui.auth;

import com.connectai.config.AppConfig;
import com.connectai.config.ThemeColors;
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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Clean, organized, and user-friendly authentication screen.
 */
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
        setSize(new Dimension(960, 640));
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeColors.MAIN_BG);

        // Left Branding & Highlights Panel (Clean white, 380px)
        mainPanel.add(createBrandingPanel(), BorderLayout.WEST);

        // Right Cards Panel (Centered Login / Register Cards)
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setOpaque(false);

        cardsPanel.add(createLoginCard(), "LOGIN");
        cardsPanel.add(createRegisterCard(), "REGISTER");

        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(380, 640));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeColors.DIVIDER),
                BorderFactory.createEmptyBorder(44, 40, 40, 40)
        ));

        // 1. Logo Header
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoRow.setOpaque(false);
        logoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoIcon = new JLabel("⚡");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        logoIcon.setForeground(ThemeColors.PRIMARY_ACCENT);

        JLabel logoText = new JLabel("ConnectAI");
        logoText.setFont(new Font("SansSerif", Font.BOLD, 26));
        logoText.setForeground(ThemeColors.PRIMARY_TEXT);

        logoRow.add(logoIcon);
        logoRow.add(logoText);

        JLabel tagline = new JLabel("AI-Powered Realtime Messenger");
        tagline.setFont(ThemeFonts.BODY_MEDIUM);
        tagline.setForeground(ThemeColors.MUTED_TEXT);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagline.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));

        panel.add(logoRow);
        panel.add(tagline);
        panel.add(Box.createVerticalStrut(36));

        // 2. Feature Highlights (Clean bullet list with icons)
        panel.add(createFeatureItem("💬", "Realtime Team Messaging", "Instant non-blocking communication across direct chats and channels."));
        panel.add(Box.createVerticalStrut(18));
        panel.add(createFeatureItem("🤖", "Built-in AI Assistant", "Catch Me Up conversation summaries and tone rephrasing powered by OpenAI."));
        panel.add(Box.createVerticalStrut(18));
        panel.add(createFeatureItem("🎙", "Voice Notes & Files", "Share high-fidelity audio recordings and documents with one click."));
        panel.add(Box.createVerticalStrut(18));
        panel.add(createFeatureItem("📊", "Interactive Team Polls", "Gather instant team feedback with single & multiple choice voting."));

        panel.add(Box.createVerticalGlue());

        // 3. Status Badge
        JPanel badge = new RoundedPanel(12, ThemeColors.PRIMARY_ACCENT_LIGHT, Color.decode("#BFDBFE"));
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));
        badge.setMaximumSize(new Dimension(300, 32));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel badgeText = new JLabel("⚡ Offline Mock & Supabase Cloud Ready");
        badgeText.setFont(ThemeFonts.CAPTION);
        badgeText.setForeground(ThemeColors.PRIMARY_ACCENT);
        badge.add(badgeText);

        panel.add(badge);

        return panel;
    }

    private JPanel createFeatureItem(String icon, String title, String desc) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setOpaque(false);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(320, 52));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeFonts.BODY_BOLD);
        titleLabel.setForeground(ThemeColors.PRIMARY_TEXT);

        JLabel descLabel = new JLabel("<html><body style='width: 230px; color: #64748B; font-size: 11px;'>" + desc + "</body></html>");

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(descLabel);

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);

        return item;
    }

    private JPanel createLoginCard() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        // Clean elevated white card
        JPanel card = new RoundedPanel(16, Color.WHITE, ThemeColors.CARD_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(400, 480));
        card.setMaximumSize(new Dimension(420, 500));
        card.setBorder(BorderFactory.createEmptyBorder(32, 34, 30, 34));

        // Header
        JLabel h1 = new JLabel("Welcome back");
        h1.setFont(new Font("SansSerif", Font.BOLD, 22));
        h1.setForeground(ThemeColors.PRIMARY_TEXT);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your ConnectAI account");
        sub.setFont(ThemeFonts.BODY_MEDIUM);
        sub.setForeground(ThemeColors.MUTED_TEXT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(h1);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(22));

        // Email Label & Input Field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(ThemeFonts.BODY_BOLD);
        emailLabel.setForeground(ThemeColors.SECONDARY_TEXT);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginEmailField = new RoundedTextField("name@work-email.com");
        loginEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(loginEmailField);
        card.add(Box.createVerticalStrut(14));

        // Password Label & Forgot Password Link
        JPanel passLabelRow = new JPanel(new BorderLayout());
        passLabelRow.setOpaque(false);
        passLabelRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(ThemeFonts.BODY_BOLD);
        passLabel.setForeground(ThemeColors.SECONDARY_TEXT);

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

        passLabelRow.add(passLabel, BorderLayout.WEST);
        passLabelRow.add(forgotLabel, BorderLayout.EAST);

        loginPasswordField = new PasswordFieldWithToggle("Enter password");
        loginPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(passLabelRow);
        card.add(Box.createVerticalStrut(6));
        card.add(loginPasswordField);
        card.add(Box.createVerticalStrut(20));

        // Primary Action: Log In Button (Full width)
        loginButton = new PremiumButton("Log In", ThemeColors.PRIMARY_ACCENT, Color.WHITE);
        loginButton.setFont(ThemeFonts.BODY_BOLD);
        loginButton.setPreferredSize(new Dimension(332, 42));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());

        // Secondary Action: Explore with Mock Data Button (Full width, soft blue)
        PremiumButton mockModeButton = new PremiumButton("⚡ Explore Demo / Mock Workspace", ThemeColors.PRIMARY_ACCENT_LIGHT, ThemeColors.PRIMARY_ACCENT);
        mockModeButton.setFont(ThemeFonts.BODY_BOLD);
        mockModeButton.setPreferredSize(new Dimension(332, 40));
        mockModeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mockModeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        mockModeButton.addActionListener(e -> {
            AppConfig.setMockMode(true);
            dispose();
            SwingUtilities.invokeLater(() -> new MainChatFrame().setVisible(true));
        });

        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(mockModeButton);
        card.add(Box.createVerticalStrut(18));

        // Switch to Sign Up
        JPanel switchBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchBox.setOpaque(false);
        switchBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prompt = new JLabel("Don't have an account?");
        prompt.setFont(ThemeFonts.BODY_SMALL);
        prompt.setForeground(ThemeColors.MUTED_TEXT);

        JLabel switchRegister = new JLabel("Sign up");
        switchRegister.setFont(ThemeFonts.BODY_BOLD);
        switchRegister.setForeground(ThemeColors.PRIMARY_ACCENT);
        switchRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(cardsPanel, "REGISTER");
            }
        });

        switchBox.add(prompt);
        switchBox.add(switchRegister);

        card.add(switchBox);

        container.add(card);
        return container;
    }

    private JPanel createRegisterCard() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        JPanel card = new RoundedPanel(16, Color.WHITE, ThemeColors.CARD_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 560));
        card.setMaximumSize(new Dimension(440, 580));
        card.setBorder(BorderFactory.createEmptyBorder(28, 34, 26, 34));

        JLabel h1 = new JLabel("Create an Account");
        h1.setFont(new Font("SansSerif", Font.BOLD, 22));
        h1.setForeground(ThemeColors.PRIMARY_TEXT);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Get started with ConnectAI today");
        sub.setFont(ThemeFonts.BODY_MEDIUM);
        sub.setForeground(ThemeColors.MUTED_TEXT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(h1);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));

        regNameField = new RoundedTextField("Display Name (e.g. Alex Rivera)");
        regNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        regUserField = new RoundedTextField("Username (unique)");
        regUserField.setAlignmentX(Component.LEFT_ALIGNMENT);
        regEmailField = new RoundedTextField("Email address");
        regEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        regPasswordField = new PasswordFieldWithToggle("Password (min 6 chars)");
        regPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        regConfirmPasswordField = new PasswordFieldWithToggle("Confirm Password");
        regConfirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(createFieldBlock("Full Name", regNameField));
        card.add(Box.createVerticalStrut(8));
        card.add(createFieldBlock("Username", regUserField));
        card.add(Box.createVerticalStrut(8));
        card.add(createFieldBlock("Email Address", regEmailField));
        card.add(Box.createVerticalStrut(8));
        card.add(createFieldBlock("Password", regPasswordField));
        card.add(Box.createVerticalStrut(8));
        card.add(createFieldBlock("Confirm Password", regConfirmPasswordField));
        card.add(Box.createVerticalStrut(16));

        registerButton = new PremiumButton("Create Account", ThemeColors.PRIMARY_ACCENT, Color.WHITE);
        registerButton.setFont(ThemeFonts.BODY_BOLD);
        registerButton.setPreferredSize(new Dimension(352, 42));
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.addActionListener(e -> performRegistration());

        card.add(registerButton);
        card.add(Box.createVerticalStrut(14));

        // Switch to Log In
        JPanel switchBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchBox.setOpaque(false);
        switchBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prompt = new JLabel("Already have an account?");
        prompt.setFont(ThemeFonts.BODY_SMALL);
        prompt.setForeground(ThemeColors.MUTED_TEXT);

        JLabel switchLogin = new JLabel("Log in");
        switchLogin.setFont(ThemeFonts.BODY_BOLD);
        switchLogin.setForeground(ThemeColors.PRIMARY_ACCENT);
        switchLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(cardsPanel, "LOGIN");
            }
        });

        switchBox.add(prompt);
        switchBox.add(switchLogin);
        card.add(switchBox);

        container.add(card);
        return container;
    }

    private JPanel createFieldBlock(String labelText, Component field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(labelText);
        l.setFont(ThemeFonts.BODY_SMALL);
        l.setForeground(ThemeColors.SECONDARY_TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        block.add(l);
        block.add(Box.createVerticalStrut(4));
        block.add(field);

        return block;
    }

    private void performLogin() {
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getPasswordString();

        if (email.isBlank() || password.isBlank() || email.contains("name@work-email.com")) {
            ToastManager.showToast(this, "Please fill in email and password", ToastManager.ToastType.WARNING);
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
