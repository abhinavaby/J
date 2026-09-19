package com.connectai.ui.chat;

import com.connectai.config.AppConfig;
import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeDimensions;
import com.connectai.model.Conversation;
import com.connectai.model.Message;
import com.connectai.realtime.RealtimeEventListener;
import com.connectai.realtime.SupabaseRealtimeClient;
import com.connectai.service.AuthService;
import com.connectai.service.ChatService;
import com.connectai.ui.components.EmptyStatePanel;
import com.connectai.ui.components.RoundedPanel;
import com.connectai.ui.components.StatusBadge;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class MainChatFrame extends JFrame implements RealtimeEventListener {
    private Conversation activeConversation;

    private ConversationListPanel leftSidebarPanel;
    private ChatHeaderPanel chatHeaderPanel;
    private JPanel messageListContainer;
    private JScrollPane messageScrollPane;
    private ComposerPanel composerPanel;
    private RightInfoPanel rightInfoPanel;

    private CardLayout centerCardLayout;
    private JPanel centerCardContainer;
    private StatusBadge connectionStatusBadge;

    public MainChatFrame() {
        setTitle(AppConfig.getAppName() + " — Desktop Messenger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ThemeDimensions.WINDOW_DEFAULT_SIZE);
        setMinimumSize(ThemeDimensions.WINDOW_MIN_SIZE);
        setLocationRelativeTo(null);

        JPanel rootPanel = new RoundedPanel(0, ThemeColors.MAIN_BG);
        rootPanel.setLayout(new BorderLayout());

        // Left Sidebar
        leftSidebarPanel = new ConversationListPanel(this::openConversation);

        // Center Chat Section
        JPanel centerSection = new JPanel(new BorderLayout());
        centerSection.setBackground(ThemeColors.MESSAGE_AREA);

        chatHeaderPanel = new ChatHeaderPanel(this::toggleRightPanel);
        composerPanel = new ComposerPanel(this::reloadActiveMessages);

        messageListContainer = new JPanel();
        messageListContainer.setLayout(new BoxLayout(messageListContainer, BoxLayout.Y_AXIS));
        messageListContainer.setBackground(ThemeColors.MESSAGE_AREA);

        messageScrollPane = new JScrollPane(messageListContainer);
        messageScrollPane.setBorder(null);
        messageScrollPane.setOpaque(false);
        messageScrollPane.getViewport().setOpaque(false);

        centerCardLayout = new CardLayout();
        centerCardContainer = new JPanel(centerCardLayout);
        centerCardContainer.setOpaque(false);

        JPanel activeChatView = new JPanel(new BorderLayout());
        activeChatView.setOpaque(false);
        activeChatView.add(chatHeaderPanel, BorderLayout.NORTH);
        activeChatView.add(messageScrollPane, BorderLayout.CENTER);
        activeChatView.add(composerPanel, BorderLayout.SOUTH);

        EmptyStatePanel emptyStateView = new EmptyStatePanel("💬", "No conversation selected", "Select a contact or group from the left panel to start messaging.");

        centerCardContainer.add(emptyStateView, "EMPTY");
        centerCardContainer.add(activeChatView, "CHAT");

        // Right Info Panel
        rightInfoPanel = new RightInfoPanel();
        rightInfoPanel.setVisible(true);

        // Top Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(false);
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        connectionStatusBadge = new StatusBadge("Connected", ThemeColors.SUCCESS_ONLINE);
        statusBar.add(connectionStatusBadge, BorderLayout.EAST);

        rootPanel.add(leftSidebarPanel, BorderLayout.WEST);
        rootPanel.add(centerCardContainer, BorderLayout.CENTER);
        rootPanel.add(rightInfoPanel, BorderLayout.EAST);

        setContentPane(rootPanel);

        // Realtime client subscription
        SupabaseRealtimeClient.getInstance().addListener(this);
        SupabaseRealtimeClient.getInstance().connect();

        centerCardLayout.show(centerCardContainer, "EMPTY");
    }

    private void openConversation(Conversation conv) {
        this.activeConversation = conv;
        if (conv == null) {
            centerCardLayout.show(centerCardContainer, "EMPTY");
            return;
        }

        chatHeaderPanel.setConversation(conv);
        composerPanel.setConversationId(conv.getId());
        rightInfoPanel.setConversation(conv);
        centerCardLayout.show(centerCardContainer, "CHAT");

        reloadActiveMessages();
    }

    private void reloadActiveMessages() {
        if (activeConversation == null) return;

        new SwingWorker<List<Message>, Void>() {
            @Override
            protected List<Message> doInBackground() throws Exception {
                return ChatService.getInstance().loadMessages(activeConversation.getId(), 50, 0).get();
            }

            @Override
            protected void done() {
                try {
                    List<Message> msgs = get();
                    messageListContainer.removeAll();
                    String currentUserId = AuthService.getInstance().getCurrentUser() != null ? AuthService.getInstance().getCurrentUser().getId() : "";

                    // Messages come ordered desc from DB, reverse to render chronologically
                    for (int i = msgs.size() - 1; i >= 0; i--) {
                        Message m = msgs.get(i);
                        boolean isOutgoing = m.getSenderId().equals(currentUserId);
                        MessageBubble bubble = new MessageBubble(
                                m, isOutgoing,
                                replyMsg -> composerPanel.setReplyToMessage(replyMsg),
                                editMsg -> composerPanel.setEditTargetMessage(editMsg),
                                deleteMsg -> ChatService.getInstance().deleteMessage(deleteMsg.getId()).thenRun(() -> reloadActiveMessages())
                        );
                        messageListContainer.add(bubble);
                    }

                    messageListContainer.revalidate();
                    messageListContainer.repaint();
                    scrollToBottom();
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            var bar = messageScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private void toggleRightPanel() {
        rightInfoPanel.setVisible(!rightInfoPanel.isVisible());
        revalidate();
        repaint();
    }

    @Override
    public void onNewMessage(Message message) {
        if (activeConversation != null && message.getConversationId().equals(activeConversation.getId())) {
            reloadActiveMessages();
        }
        leftSidebarPanel.refreshConversations();
    }

    @Override
    public void onConnectionStateChanged(boolean isConnected) {
        SwingUtilities.invokeLater(() -> {
            if (isConnected) {
                connectionStatusBadge.setStatus("Connected", ThemeColors.SUCCESS_ONLINE);
            } else {
                connectionStatusBadge.setStatus("Reconnecting", ThemeColors.WARNING);
            }
        });
    }
}
