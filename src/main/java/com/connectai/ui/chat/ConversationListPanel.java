package com.connectai.ui.chat;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.model.Conversation;
import com.connectai.model.ConversationMember;
import com.connectai.model.Profile;
import com.connectai.service.AuthService;
import com.connectai.service.ChatService;
import com.connectai.ui.components.AvatarComponent;
import com.connectai.ui.components.IconButton;
import com.connectai.ui.components.PremiumButton;
import com.connectai.ui.components.RoundedPanel;
import com.connectai.ui.components.RoundedTextField;
import com.connectai.ui.group.CreateGroupDialog;
import com.connectai.ui.group.JoinGroupDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class ConversationListPanel extends JPanel {
    private DefaultListModel<Conversation> listModel;
    private JList<Conversation> conversationJList;
    private RoundedTextField searchField;
    private List<Conversation> allConversations = new ArrayList<>();
    private Consumer<Conversation> onConversationSelected;

    public ConversationListPanel(Consumer<Conversation> onConversationSelected) {
        this.onConversationSelected = onConversationSelected;
        setLayout(new BorderLayout());
        setBackground(ThemeColors.SIDEBAR_BG);
        setPreferredSize(new Dimension(340, 850));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createListScroll(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        refreshConversations();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new javax.swing.BoxLayout(header, javax.swing.BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        // User profile header
        Profile p = AuthService.getInstance().getCurrentProfile();
        String name = p != null ? p.getEffectiveName() : "User";

        JPanel userRow = new JPanel(new BorderLayout(12, 0));
        userRow.setOpaque(false);

        AvatarComponent avatar = new AvatarComponent(40, name, true);
        avatar.setOnline(true);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(ThemeFonts.TITLE_SMALL);
        nameLabel.setForeground(ThemeColors.PRIMARY_TEXT);

        JLabel statusLabel = new JLabel("Online");
        statusLabel.setFont(ThemeFonts.CAPTION);
        statusLabel.setForeground(ThemeColors.SUCCESS_ONLINE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(statusLabel);

        IconButton settingsBtn = new IconButton("⚙", "Settings");
        settingsBtn.addActionListener(e -> {
            Frame top = (Frame) SwingUtilities.getWindowAncestor(this);
            new com.connectai.ui.components.SettingsDialog(top).setVisible(true);
        });

        userRow.add(avatar, BorderLayout.WEST);
        userRow.add(textPanel, BorderLayout.CENTER);
        userRow.add(settingsBtn, BorderLayout.EAST);

        // Search bar
        searchField = new RoundedTextField("Search conversations...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterList(searchField.getText().trim());
            }
        });

        header.add(userRow);
        header.add(javax.swing.Box.createVerticalStrut(14));
        header.add(searchField);

        return header;
    }

    private JScrollPane createListScroll() {
        listModel = new DefaultListModel<>();
        conversationJList = new JList<>(listModel);
        conversationJList.setBackground(ThemeColors.SIDEBAR_BG);
        conversationJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conversationJList.setCellRenderer(new ConversationRenderer());

        conversationJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Conversation selected = conversationJList.getSelectedValue();
                if (selected != null && onConversationSelected != null) {
                    onConversationSelected.accept(selected);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(conversationJList);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new java.awt.GridLayout(1, 2, 8, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        PremiumButton newGroupBtn = new PremiumButton("+ Group", ThemeColors.PRIMARY_ACCENT, Color.WHITE);
        newGroupBtn.setFont(ThemeFonts.BODY_SMALL);
        newGroupBtn.addActionListener(e -> {
            Frame top = (Frame) SwingUtilities.getWindowAncestor(this);
            new CreateGroupDialog(top, this::refreshConversations).setVisible(true);
        });

        PremiumButton joinGroupBtn = new PremiumButton("Join Code", ThemeColors.ELEVATED_SURFACE, ThemeColors.PRIMARY_TEXT);
        joinGroupBtn.setFont(ThemeFonts.BODY_SMALL);
        joinGroupBtn.addActionListener(e -> {
            Frame top = (Frame) SwingUtilities.getWindowAncestor(this);
            new JoinGroupDialog(top, this::refreshConversations).setVisible(true);
        });

        footer.add(newGroupBtn);
        footer.add(joinGroupBtn);
        return footer;
    }

    public void refreshConversations() {
        ChatService.getInstance().loadUserConversations().thenAccept(memberships -> {
            SwingUtilities.invokeLater(() -> {
                allConversations.clear();
                listModel.clear();
                for (ConversationMember cm : memberships) {
                    if (cm.getConversation() != null) {
                        allConversations.add(cm.getConversation());
                    } else if (cm.getProfile() != null) {
                        allConversations.add(createDummyFromMember(cm));
                    }
                }
                // Fallback demo row if empty
                if (allConversations.isEmpty()) {
                    Conversation c = new Conversation();
                    c.setId("general-lounge");
                    c.setName("General Lounge");
                    c.setDescription("Public group chat");
                    c.setType(com.connectai.model.ConversationType.GROUP);
                    allConversations.add(c);
                }
                filterList(searchField != null ? searchField.getText().trim() : "");
            });
        });
    }

    private Conversation createDummyFromMember(ConversationMember cm) {
        Conversation c = new Conversation();
        c.setId(cm.getConversationId());
        c.setName(cm.getProfile().getEffectiveName());
        c.setDirectPartnerProfile(cm.getProfile());
        c.setType(com.connectai.model.ConversationType.DIRECT);
        return c;
    }

    private void filterList(String query) {
        listModel.clear();
        for (Conversation c : allConversations) {
            if (query.isEmpty() || c.getDisplayTitle().toLowerCase().contains(query.toLowerCase())) {
                listModel.addElement(c);
            }
        }
    }

    private static class ConversationRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Conversation c = (Conversation) value;
            JPanel panel = new RoundedPanel(8, isSelected ? ThemeColors.SELECTION_OVERLAY : ThemeColors.SIDEBAR_BG);
            panel.setLayout(new BorderLayout(12, 0));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

            AvatarComponent avatar = new AvatarComponent(42, c.getDisplayTitle(), true);

            JLabel name = new JLabel(c.getDisplayTitle());
            name.setFont(ThemeFonts.BODY_BOLD);
            name.setForeground(ThemeColors.PRIMARY_TEXT);

            JLabel sub = new JLabel(c.getLatestMessage() != null ? c.getLatestMessage().getContent() : "Tap to open chat");
            sub.setFont(ThemeFonts.BODY_SMALL);
            sub.setForeground(ThemeColors.MUTED_TEXT);

            JPanel text = new JPanel();
            text.setLayout(new javax.swing.BoxLayout(text, javax.swing.BoxLayout.Y_AXIS));
            text.setOpaque(false);
            text.add(name);
            text.add(sub);

            panel.add(avatar, BorderLayout.WEST);
            panel.add(text, BorderLayout.CENTER);

            return panel;
        }
    }
}
