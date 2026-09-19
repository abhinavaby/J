package com.connectai.ui.media;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.ui.components.PremiumButton;
import com.connectai.ui.components.RoundedPanel;
import com.connectai.ui.components.RoundedTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AttachmentPreviewDialog extends JDialog {
    private File file;
    private RoundedTextField captionField;
    private java.util.function.BiConsumer<File, String> onSendCallback;

    public AttachmentPreviewDialog(Frame owner, File file, java.util.function.BiConsumer<File, String> onSendCallback) {
        super(owner, "Attachment Preview", true);
        this.file = file;
        this.onSendCallback = onSendCallback;

        setSize(new Dimension(440, 360));
        setLocationRelativeTo(owner);

        JPanel mainPanel = new RoundedPanel(0, ThemeColors.MAIN_BG);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("📎 Send Attachment");
        title.setFont(ThemeFonts.TITLE_MEDIUM);
        title.setForeground(ThemeColors.PRIMARY_TEXT);

        JPanel fileCard = new RoundedPanel(12, ThemeColors.ELEVATED_SURFACE, ThemeColors.CARD_BORDER);
        fileCard.setLayout(new BorderLayout(16, 0));
        fileCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel iconLabel = new JLabel(getFileIcon(file.getName()), SwingConstants.CENTER);
        iconLabel.setFont(ThemeFonts.TITLE_LARGE.deriveFont(36.0f));

        JLabel nameLabel = new JLabel(file.getName());
        nameLabel.setFont(ThemeFonts.BODY_BOLD);
        nameLabel.setForeground(ThemeColors.PRIMARY_TEXT);

        JLabel sizeLabel = new JLabel(formatFileSize(file.length()));
        sizeLabel.setFont(ThemeFonts.BODY_SMALL);
        sizeLabel.setForeground(ThemeColors.MUTED_TEXT);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(nameLabel);
        infoPanel.add(sizeLabel);

        fileCard.add(iconLabel, BorderLayout.WEST);
        fileCard.add(infoPanel, BorderLayout.CENTER);

        captionField = new RoundedTextField("Add a caption (optional)");

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(fileCard);
        center.add(javax.swing.Box.createVerticalStrut(16));
        center.add(captionField);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        PremiumButton cancelBtn = new PremiumButton("Cancel", ThemeColors.ELEVATED_SURFACE, ThemeColors.PRIMARY_TEXT);
        cancelBtn.addActionListener(e -> dispose());

        PremiumButton sendBtn = new PremiumButton("Send File", ThemeColors.PRIMARY_ACCENT, ThemeColors.PRIMARY_TEXT);
        sendBtn.addActionListener(e -> {
            dispose();
            if (onSendCallback != null) {
                onSendCallback.accept(file, captionField.getText().trim());
            }
        });

        footer.add(cancelBtn, BorderLayout.WEST);
        footer.add(sendBtn, BorderLayout.EAST);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private String getFileIcon(String filename) {
        String name = filename.toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) return "🖼";
        if (name.endsWith(".pdf")) return "📄";
        if (name.endsWith(".docx") || name.endsWith(".doc")) return "📝";
        if (name.endsWith(".zip")) return "📦";
        if (name.endsWith(".wav") || name.endsWith(".mp3")) return "🎵";
        if (name.endsWith(".mp4")) return "🎬";
        return "📁";
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}
