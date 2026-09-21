package com.connectai.ui.chat;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.model.Message;
import com.connectai.model.MessageStatus;
import com.connectai.model.MessageType;

import com.connectai.ui.components.RoundedPanel;
import com.connectai.ui.media.AudioPlayerComponent;

import com.connectai.ui.poll.PollCardComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class MessageBubble extends JPanel {
    private Message message;
    private boolean isOutgoing;
    private Consumer<Message> onReplyCallback;
    private Consumer<Message> onEditCallback;
    private Consumer<Message> onDeleteCallback;

    public MessageBubble(Message message, boolean isOutgoing, Consumer<Message> onReplyCallback,
            Consumer<Message> onEditCallback, Consumer<Message> onDeleteCallback) {
        this.message = message;
        this.isOutgoing = isOutgoing;
        this.onReplyCallback = onReplyCallback;
        this.onEditCallback = onEditCallback;
        this.onDeleteCallback = onDeleteCallback;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));

        Color bg = isOutgoing ? ThemeColors.OUTGOING_BUBBLE : ThemeColors.INCOMING_BUBBLE;
        JPanel bubbleCard = new RoundedPanel(14, bg);
        bubbleCard.setLayout(new BoxLayout(bubbleCard, BoxLayout.Y_AXIS));
        bubbleCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Sender name for incoming group messages
        if (!isOutgoing && message.getSenderProfile() != null) {
            JLabel senderLabel = new JLabel(message.getSenderProfile().getEffectiveName());
            senderLabel.setFont(ThemeFonts.BODY_BOLD);
            senderLabel.setForeground(ThemeColors.SECONDARY_ACCENT);
            bubbleCard.add(senderLabel);
            bubbleCard.add(javax.swing.Box.createVerticalStrut(4));
        }

        // Reply preview if present
        if (message.getReplyMessage() != null) {
            JPanel replyPanel = new RoundedPanel(6, new Color(0, 0, 0, 40));
            replyPanel.setLayout(new BorderLayout());
            replyPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            JLabel rText = new JLabel("↩ " + message.getReplyMessage().getContent());
            rText.setFont(ThemeFonts.BODY_SMALL);
            rText.setForeground(ThemeColors.SECONDARY_TEXT);
            replyPanel.add(rText, BorderLayout.CENTER);
            bubbleCard.add(replyPanel);
            bubbleCard.add(javax.swing.Box.createVerticalStrut(6));
        }

        // Message content body
        if (message.getMessageType() == MessageType.POLL && message.getPoll() != null) {
            bubbleCard.add(new PollCardComponent(message.getPoll()));
        } else if ((message.getMessageType() == MessageType.AUDIO || message.getMessageType() == MessageType.VOICE)
                && message.getAttachment() != null) {
            bubbleCard.add(new AudioPlayerComponent(message.getAttachment()));
        } else {
            String text = message.isDeleted() ? "<i>" + message.getContent() + "</i>" : message.getContent();
            JLabel textLabel = new JLabel(
                    "<html><body style='width: 320px; color: #F8FAFC;'>" + text + "</body></html>");
            textLabel.setFont(ThemeFonts.BODY_MEDIUM);
            textLabel.setForeground(ThemeColors.PRIMARY_TEXT);
            bubbleCard.add(textLabel);
        }

        // Footer timestamp & status indicators
        JPanel footer = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 0));
        footer.setOpaque(false);

        String time = message.getCreatedAt() != null && message.getCreatedAt().length() >= 16
                ? message.getCreatedAt().substring(11, 16)
                : "Just now";
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(ThemeFonts.CAPTION);
        timeLabel.setForeground(ThemeColors.MUTED_TEXT);
        footer.add(timeLabel);

        if (isOutgoing) {
            JLabel statusIcon = new JLabel(getStatusCheckmark(message.getStatus()));
            statusIcon.setFont(ThemeFonts.CAPTION);
            statusIcon.setForeground(
                    message.getStatus() == MessageStatus.READ ? ThemeColors.SECONDARY_ACCENT : ThemeColors.MUTED_TEXT);
            footer.add(statusIcon);
        }

        bubbleCard.add(javax.swing.Box.createVerticalStrut(4));
        bubbleCard.add(footer);

        // Position bubble left or right
        JPanel alignContainer = new JPanel(new BorderLayout());
        alignContainer.setOpaque(false);
        if (isOutgoing) {
            alignContainer.add(bubbleCard, BorderLayout.EAST);
        } else {
            alignContainer.add(bubbleCard, BorderLayout.WEST);
        }

        // Context Menu
        JPopupMenu popup = createContextMenu();
        bubbleCard.setComponentPopupMenu(popup);

        add(alignContainer, BorderLayout.CENTER);
    }

    private String getStatusCheckmark(MessageStatus status) {
        if (status == MessageStatus.SENDING)
            return "🕒";
        if (status == MessageStatus.SENT)
            return "✓";
        if (status == MessageStatus.DELIVERED)
            return "✓✓";
        if (status == MessageStatus.READ)
            return "✓✓";
        return "✓";
    }

    private JPopupMenu createContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(ThemeColors.ELEVATED_SURFACE);

        JMenuItem replyItem = new JMenuItem("↩ Reply");
        replyItem.addActionListener(e -> {
            if (onReplyCallback != null)
                onReplyCallback.accept(message);
        });

        JMenuItem copyItem = new JMenuItem("📋 Copy");
        copyItem.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(message.getContent()),
                    null);
        });

        menu.add(replyItem);
        menu.add(copyItem);

        if (isOutgoing && !message.isDeleted()) {
            JMenuItem editItem = new JMenuItem("✏ Edit");
            editItem.addActionListener(e -> {
                if (onEditCallback != null)
                    onEditCallback.accept(message);
            });

            JMenuItem deleteItem = new JMenuItem("🗑 Delete");
            deleteItem.addActionListener(e -> {
                if (onDeleteCallback != null)
                    onDeleteCallback.accept(message);
            });

            menu.add(editItem);
            menu.add(deleteItem);
        }

        return menu;
    }
}
