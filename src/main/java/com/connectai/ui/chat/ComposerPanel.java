package com.connectai.ui.chat;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.model.Message;
import com.connectai.service.AttachmentService;
import com.connectai.service.AudioService;
import com.connectai.service.ChatService;
import com.connectai.ui.components.AIRephrasePopup;
import com.connectai.ui.components.IconButton;
import com.connectai.ui.components.PremiumButton;
import com.connectai.ui.components.RoundedPanel;
import com.connectai.ui.components.ToastManager;
import com.connectai.ui.media.AttachmentPreviewDialog;
import com.connectai.ui.media.VoiceRecorderPanel;
import com.connectai.ui.poll.CreatePollDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ComposerPanel extends JPanel {
    private String conversationId;
    private Message replyToMessage;
    private Message editTargetMessage;

    private JTextArea inputArea;
    private JPanel previewHeaderPanel;
    private JPanel activeComposerCard;
    private Runnable onMessageSentCallback;

    public ComposerPanel(Runnable onMessageSentCallback) {
        this.onMessageSentCallback = onMessageSentCallback;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        renderNormalComposer();
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setReplyToMessage(Message replyMsg) {
        this.replyToMessage = replyMsg;
        renderReplyOrEditPreview("Replying to: " + replyMsg.getContent());
    }

    public void setEditTargetMessage(Message editMsg) {
        this.editTargetMessage = editMsg;
        inputArea.setText(editMsg.getContent());
        renderReplyOrEditPreview("Editing message: " + editMsg.getContent());
    }

    private void renderNormalComposer() {
        removeAll();

        activeComposerCard = new RoundedPanel(16, ThemeColors.ELEVATED_SURFACE, ThemeColors.CARD_BORDER);
        activeComposerCard.setLayout(new BorderLayout(8, 0));
        activeComposerCard.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Reply / Edit preview header
        previewHeaderPanel = new JPanel(new BorderLayout());
        previewHeaderPanel.setOpaque(false);
        previewHeaderPanel.setVisible(false);

        inputArea = new JTextArea(1, 40);
        inputArea.setFont(ThemeFonts.BODY_MEDIUM);
        inputArea.setForeground(ThemeColors.PRIMARY_TEXT);
        inputArea.setCaretColor(ThemeColors.PRIMARY_ACCENT);
        inputArea.setBackground(ThemeColors.ELEVATED_SURFACE);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setOpaque(false);
        inputArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        inputArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        inputArea.append("\n");
                    } else {
                        e.consume();
                        sendCurrentText();
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(inputArea);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(500, 40));

        // Left attachment tools
        JPanel leftTools = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 0));
        leftTools.setOpaque(false);

        IconButton attachBtn = new IconButton("📎", "Attach File");
        attachBtn.addActionListener(e -> selectAndAttachFile());

        IconButton pollBtn = new IconButton("📊", "Create Poll");
        pollBtn.addActionListener(e -> openPollDialog());

        leftTools.add(attachBtn);
        leftTools.add(pollBtn);

        // Right tools
        JPanel rightTools = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 0));
        rightTools.setOpaque(false);

        IconButton aiBtn = new IconButton("✨", "AI Rephrase");
        aiBtn.addActionListener(e -> openAIRephrasePopup(aiBtn));

        IconButton micBtn = new IconButton("🎙", "Record Voice Note");
        micBtn.addActionListener(e -> startVoiceRecorder());

        PremiumButton sendBtn = new PremiumButton("Send", ThemeColors.PRIMARY_ACCENT, ThemeColors.ACCENT_TEXT);
        sendBtn.setPreferredSize(new Dimension(68, 36));
        sendBtn.addActionListener(e -> sendCurrentText());

        rightTools.add(aiBtn);
        rightTools.add(micBtn);
        rightTools.add(sendBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(previewHeaderPanel, BorderLayout.NORTH);
        centerPanel.add(scroll, BorderLayout.CENTER);

        activeComposerCard.add(leftTools, BorderLayout.WEST);
        activeComposerCard.add(centerPanel, BorderLayout.CENTER);
        activeComposerCard.add(rightTools, BorderLayout.EAST);

        add(activeComposerCard, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void renderReplyOrEditPreview(String text) {
        previewHeaderPanel.removeAll();
        previewHeaderPanel.setVisible(true);

        JLabel label = new JLabel(text);
        label.setFont(ThemeFonts.CAPTION);
        label.setForeground(ThemeColors.SECONDARY_ACCENT);

        IconButton cancelBtn = new IconButton("✕", "Cancel");
        cancelBtn.setPreferredSize(new Dimension(20, 20));
        cancelBtn.addActionListener(e -> clearPreview());

        previewHeaderPanel.add(label, BorderLayout.CENTER);
        previewHeaderPanel.add(cancelBtn, BorderLayout.EAST);

        revalidate();
        repaint();
    }

    private void clearPreview() {
        replyToMessage = null;
        editTargetMessage = null;
        previewHeaderPanel.setVisible(false);
        inputArea.setText("");
        revalidate();
        repaint();
    }

    private void sendCurrentText() {
        String text = inputArea.getText().trim();
        if (text.isBlank() || conversationId == null)
            return;

        if (editTargetMessage != null) {
            ChatService.getInstance().editMessage(editTargetMessage.getId(), text);
            clearPreview();
            if (onMessageSentCallback != null)
                onMessageSentCallback.run();
            return;
        }

        String replyId = replyToMessage != null ? replyToMessage.getId() : null;
        inputArea.setText("");
        clearPreview();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ChatService.getInstance().sendTextMessage(conversationId, text, replyId).get();
                return null;
            }

            @Override
            protected void done() {
                if (onMessageSentCallback != null)
                    onMessageSentCallback.run();
            }
        }.execute();
    }

    private void selectAndAttachFile() {
        if (conversationId == null)
            return;
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            Frame top = (Frame) SwingUtilities.getWindowAncestor(this);
            new AttachmentPreviewDialog(top, selectedFile, (file, caption) -> {
                ToastManager.showToast(this, "Uploading file...", ToastManager.ToastType.INFO);
                AttachmentService.getInstance().uploadAndSendFile(conversationId, file, caption, null, 0)
                        .thenAccept(msg -> {
                            if (onMessageSentCallback != null)
                                onMessageSentCallback.run();
                        });
            }).setVisible(true);
        }
    }

    private void openPollDialog() {
        if (conversationId == null)
            return;
        Frame top = (Frame) SwingUtilities.getWindowAncestor(this);
        new CreatePollDialog(top, conversationId, onMessageSentCallback).setVisible(true);
    }

    private void startVoiceRecorder() {
        if (conversationId == null)
            return;
        removeAll();

        VoiceRecorderPanel voicePanel = new VoiceRecorderPanel(
                this::renderNormalComposer,
                (bytes, duration) -> {
                    ToastManager.showToast(this, "Sending voice note...", ToastManager.ToastType.INFO);
                    AudioService.getInstance().sendVoiceNote(conversationId, bytes, duration)
                            .thenAccept(msg -> {
                                renderNormalComposer();
                                if (onMessageSentCallback != null)
                                    onMessageSentCallback.run();
                            });
                });

        add(voicePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void openAIRephrasePopup(IconButton triggerBtn) {
        String draft = inputArea.getText().trim();
        if (draft.isBlank()) {
            ToastManager.showToast(this, "Type a draft message first to rephrase with AI",
                    ToastManager.ToastType.WARNING);
            return;
        }

        AIRephrasePopup popup = new AIRephrasePopup(draft, rephrased -> {
            inputArea.setText(rephrased);
        });
        popup.show(triggerBtn, 0, -120);
    }
}
