package com.connectai.ui.chat;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.model.Conversation;
import com.connectai.model.ConversationType;
import com.connectai.service.AIService;
import com.connectai.service.ChatService;
import com.connectai.ui.components.AISummaryDialog;
import com.connectai.ui.components.AvatarComponent;
import com.connectai.ui.components.IconButton;
import com.connectai.ui.components.PremiumButton;
import com.connectai.ui.components.ToastManager;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ChatHeaderPanel extends JPanel {
    private Conversation conversation;
    private AvatarComponent avatar;
    private JLabel titleLabel;
    private JLabel subTitleLabel;
    private Runnable onToggleRightPanel;

    public ChatHeaderPanel(Runnable onToggleRightPanel) {
        this.onToggleRightPanel = onToggleRightPanel;
        setLayout(new BorderLayout(12, 0));
        setBackground(ThemeColors.MESSAGE_AREA);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColors.DIVIDER),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        avatar = new AvatarComponent(42, "ConnectAI", true);

        titleLabel = new JLabel("Select a conversation");
        titleLabel.setFont(ThemeFonts.TITLE_MEDIUM);
        titleLabel.setForeground(ThemeColors.PRIMARY_TEXT);

        subTitleLabel = new JLabel("Online");
        subTitleLabel.setFont(ThemeFonts.CAPTION);
        subTitleLabel.setForeground(ThemeColors.MUTED_TEXT);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subTitleLabel);

        JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        PremiumButton catchMeUpBtn = new PremiumButton("✨ Catch Me Up", ThemeColors.ELEVATED_SURFACE, ThemeColors.PRIMARY_ACCENT);
        catchMeUpBtn.setFont(ThemeFonts.BODY_SMALL);
        catchMeUpBtn.addActionListener(e -> triggerCatchMeUp());

        IconButton infoBtn = new IconButton("ℹ", "Conversation Details");
        infoBtn.addActionListener(e -> {
            if (onToggleRightPanel != null) onToggleRightPanel.run();
        });

        actions.add(catchMeUpBtn);
        actions.add(infoBtn);

        add(avatar, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
        add(actions, BorderLayout.EAST);
    }

    public void setConversation(Conversation conv) {
        this.conversation = conv;
        if (conv == null) {
            titleLabel.setText("Select a conversation");
            subTitleLabel.setText("");
            return;
        }

        avatar.setDisplayName(conv.getDisplayTitle());
        titleLabel.setText(conv.getDisplayTitle());

        if (conv.getType() == ConversationType.GROUP) {
            subTitleLabel.setText("Group Conversation");
        } else {
            subTitleLabel.setText("Direct Message");
        }
    }

    private void triggerCatchMeUp() {
        if (conversation == null) return;

        Frame top = (Frame) SwingUtilities.getWindowAncestor(this);
        ToastManager.showToast(this, "Generating AI summary...", ToastManager.ToastType.INFO);

        new SwingWorker<com.connectai.model.AISummaryResponse, Void>() {
            @Override
            protected com.connectai.model.AISummaryResponse doInBackground() throws Exception {
                var msgs = ChatService.getInstance().loadMessages(conversation.getId(), 50, 0).get();
                return AIService.getInstance().summarizeConversation(conversation.getId(), msgs).get();
            }

            @Override
            protected void done() {
                try {
                    var summary = get();
                    new AISummaryDialog(top, summary).setVisible(true);
                } catch (Exception ex) {
                    ToastManager.showToast(ChatHeaderPanel.this, "Failed to generate AI summary", ToastManager.ToastType.ERROR);
                }
            }
        }.execute();
    }
}
