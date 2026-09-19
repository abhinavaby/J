package com.connectai.ui.chat;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.model.Conversation;
import com.connectai.model.ConversationMember;
import com.connectai.model.Role;
import com.connectai.service.ChatService;
import com.connectai.ui.components.AvatarComponent;
import com.connectai.ui.components.IconButton;
import com.connectai.ui.components.PremiumButton;
import com.connectai.ui.components.RoundedPanel;
import com.connectai.ui.components.ToastManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class RightInfoPanel extends JPanel {
    private Conversation conversation;
    private AvatarComponent avatar;
    private JLabel nameLabel;
    private JLabel descLabel;
    private JPanel inviteCodePanel;
    private JLabel inviteCodeLabel;
    private DefaultListModel<ConversationMember> memberListModel;
    private JList<ConversationMember> memberJList;

    public RightInfoPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.SIDEBAR_BG);
        setPreferredSize(new Dimension(320, 850));
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, ThemeColors.DIVIDER));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Profile Card
        avatar = new AvatarComponent(64, "Group", false);
        avatar.setAlignmentX(CENTER_ALIGNMENT);

        nameLabel = new JLabel("Group Name", SwingConstants.CENTER);
        nameLabel.setFont(ThemeFonts.TITLE_MEDIUM);
        nameLabel.setForeground(ThemeColors.PRIMARY_TEXT);
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);

        descLabel = new JLabel("Group Description", SwingConstants.CENTER);
        descLabel.setFont(ThemeFonts.BODY_SMALL);
        descLabel.setForeground(ThemeColors.MUTED_TEXT);
        descLabel.setAlignmentX(CENTER_ALIGNMENT);

        content.add(avatar);
        content.add(javax.swing.Box.createVerticalStrut(12));
        content.add(nameLabel);
        content.add(javax.swing.Box.createVerticalStrut(4));
        content.add(descLabel);
        content.add(javax.swing.Box.createVerticalStrut(20));

        // Invite code card
        inviteCodePanel = new RoundedPanel(10, ThemeColors.ELEVATED_SURFACE, ThemeColors.PRIMARY_ACCENT);
        inviteCodePanel.setLayout(new BorderLayout(8, 0));
        inviteCodePanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        inviteCodeLabel = new JLabel("Code: CONNECT2026");
        inviteCodeLabel.setFont(ThemeFonts.BODY_BOLD);
        inviteCodeLabel.setForeground(ThemeColors.PRIMARY_ACCENT);

        IconButton copyCodeBtn = new IconButton("📋", "Copy Invite Code");
        copyCodeBtn.addActionListener(e -> {
            if (conversation != null && conversation.getInviteCode() != null) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(conversation.getInviteCode()), null);
                ToastManager.showToast(this, "Invite code copied!", ToastManager.ToastType.SUCCESS);
            }
        });

        inviteCodePanel.add(inviteCodeLabel, BorderLayout.CENTER);
        inviteCodePanel.add(copyCodeBtn, BorderLayout.EAST);

        content.add(inviteCodePanel);
        content.add(javax.swing.Box.createVerticalStrut(20));

        // Members header
        JLabel membersHeader = new JLabel("Group Members");
        membersHeader.setFont(ThemeFonts.BODY_BOLD);
        membersHeader.setForeground(ThemeColors.SECONDARY_TEXT);
        content.add(membersHeader);
        content.add(javax.swing.Box.createVerticalStrut(8));

        // Members List
        memberListModel = new DefaultListModel<>();
        memberJList = new JList<>(memberListModel);
        memberJList.setBackground(ThemeColors.SIDEBAR_BG);
        memberJList.setCellRenderer(new MemberRenderer());

        JScrollPane memberScroll = new JScrollPane(memberJList);
        memberScroll.setBorder(null);
        memberScroll.setOpaque(false);
        memberScroll.getViewport().setOpaque(false);

        content.add(memberScroll);

        add(content, BorderLayout.CENTER);
    }

    public void setConversation(Conversation conv) {
        this.conversation = conv;
        if (conv == null) return;

        avatar.setDisplayName(conv.getDisplayTitle());
        nameLabel.setText(conv.getDisplayTitle());
        descLabel.setText(conv.getDescription() != null ? conv.getDescription() : "No description");

        if (conv.getInviteCode() != null) {
            inviteCodePanel.setVisible(true);
            inviteCodeLabel.setText("Code: " + conv.getInviteCode());
        } else {
            inviteCodePanel.setVisible(false);
        }

        loadGroupMembers();
    }

    private void loadGroupMembers() {
        if (conversation == null) return;
        ChatService.getInstance().getGroupMembers(conversation.getId()).thenAccept(members -> {
            SwingUtilities.invokeLater(() -> {
                memberListModel.clear();
                for (ConversationMember m : members) {
                    memberListModel.addElement(m);
                }
            });
        });
    }

    private class MemberRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            ConversationMember m = (ConversationMember) value;
            String name = m.getProfile() != null ? m.getProfile().getEffectiveName() : "Member";

            JPanel panel = new RoundedPanel(8, ThemeColors.ELEVATED_SURFACE);
            panel.setLayout(new BorderLayout(8, 0));
            panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            AvatarComponent av = new AvatarComponent(32, name, false);

            JLabel n = new JLabel(name);
            n.setFont(ThemeFonts.BODY_MEDIUM);
            n.setForeground(ThemeColors.PRIMARY_TEXT);

            JLabel roleBadge = new JLabel(m.getRole() != null ? m.getRole().name() : "MEMBER");
            roleBadge.setFont(ThemeFonts.BADGE);
            roleBadge.setForeground(m.getRole() == Role.OWNER ? ThemeColors.PRIMARY_ACCENT : ThemeColors.MUTED_TEXT);

            panel.add(av, BorderLayout.WEST);
            panel.add(n, BorderLayout.CENTER);
            panel.add(roleBadge, BorderLayout.EAST);

            return panel;
        }
    }
}
