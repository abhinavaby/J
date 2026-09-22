package com.connectai.ui.poll;

import com.connectai.config.ThemeColors;
import com.connectai.config.ThemeFonts;
import com.connectai.service.PollService;
import com.connectai.ui.components.IconButton;
import com.connectai.ui.components.PremiumButton;
import com.connectai.ui.components.RoundedPanel;
import com.connectai.ui.components.RoundedTextField;
import com.connectai.ui.components.ToastManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

public class CreatePollDialog extends JDialog {
    private String conversationId;
    private RoundedTextField questionField;
    private JPanel optionsContainer;
    private List<RoundedTextField> optionFields = new ArrayList<>();
    private JCheckBox multipleCheck;
    private JCheckBox anonymousCheck;
    private Runnable onSuccessCallback;

    public CreatePollDialog(Frame owner, String conversationId, Runnable onSuccessCallback) {
        super(owner, "Create New Poll", true);
        this.conversationId = conversationId;
        this.onSuccessCallback = onSuccessCallback;
        setSize(new Dimension(500, 550));
        setLocationRelativeTo(owner);

        JPanel mainPanel = new RoundedPanel(0, ThemeColors.MAIN_BG);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("📊 Create Group Poll");
        title.setFont(ThemeFonts.TITLE_MEDIUM);
        title.setForeground(ThemeColors.PRIMARY_TEXT);

        questionField = new RoundedTextField("Ask a question...");

        optionsContainer = new JPanel();
        optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));
        optionsContainer.setOpaque(false);

        // Add 2 default option fields
        addOptionRow();
        addOptionRow();

        PremiumButton addOptBtn = new PremiumButton("+ Add Option", ThemeColors.ELEVATED_SURFACE,
                ThemeColors.SECONDARY_ACCENT);
        addOptBtn.setFont(ThemeFonts.BODY_SMALL);
        addOptBtn.addActionListener(e -> addOptionRow());

        multipleCheck = new JCheckBox("Allow multiple answers", false);
        multipleCheck.setFont(ThemeFonts.BODY_MEDIUM);
        multipleCheck.setForeground(ThemeColors.PRIMARY_TEXT);
        multipleCheck.setOpaque(false);

        anonymousCheck = new JCheckBox("Anonymous voting", false);
        anonymousCheck.setFont(ThemeFonts.BODY_MEDIUM);
        anonymousCheck.setForeground(ThemeColors.PRIMARY_TEXT);
        anonymousCheck.setOpaque(false);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        body.add(questionField);
        body.add(javax.swing.Box.createVerticalStrut(14));
        body.add(new JLabel("Poll Options:") {
            {
                setFont(ThemeFonts.BODY_BOLD);
                setForeground(ThemeColors.SECONDARY_TEXT);
            }
        });
        body.add(javax.swing.Box.createVerticalStrut(8));
        body.add(optionsContainer);
        body.add(javax.swing.Box.createVerticalStrut(8));
        body.add(addOptBtn);
        body.add(javax.swing.Box.createVerticalStrut(14));
        body.add(multipleCheck);
        body.add(javax.swing.Box.createVerticalStrut(6));
        body.add(anonymousCheck);

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        PremiumButton cancelBtn = new PremiumButton("Cancel", ThemeColors.ELEVATED_SURFACE, ThemeColors.PRIMARY_TEXT);
        cancelBtn.addActionListener(e -> dispose());

        PremiumButton createBtn = new PremiumButton("Publish Poll", ThemeColors.PRIMARY_ACCENT,
                ThemeColors.ACCENT_TEXT);
        createBtn.addActionListener(e -> submit(createBtn));

        footer.add(cancelBtn, BorderLayout.WEST);
        footer.add(createBtn, BorderLayout.EAST);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addOptionRow() {
        if (optionFields.size() >= 8) {
            ToastManager.showToast(this, "Maximum 8 options allowed", ToastManager.ToastType.WARNING);
            return;
        }

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        RoundedTextField field = new RoundedTextField("Option " + (optionFields.size() + 1));
        optionFields.add(field);

        IconButton removeBtn = new IconButton("✕", "Remove option");
        removeBtn.addActionListener(e -> {
            if (optionFields.size() <= 2) {
                ToastManager.showToast(this, "A poll must have at least 2 options", ToastManager.ToastType.WARNING);
                return;
            }
            optionsContainer.remove(row);
            optionFields.remove(field);
            optionsContainer.revalidate();
            optionsContainer.repaint();
        });

        row.add(field, BorderLayout.CENTER);
        row.add(removeBtn, BorderLayout.EAST);

        optionsContainer.add(row);
        optionsContainer.revalidate();
        optionsContainer.repaint();
    }

    private void submit(PremiumButton btn) {
        String question = questionField.getText().trim();
        if (question.isBlank()) {
            ToastManager.showToast(this, "Please enter a question", ToastManager.ToastType.WARNING);
            return;
        }

        List<String> options = new ArrayList<>();
        for (RoundedTextField field : optionFields) {
            String txt = field.getText().trim();
            if (!txt.isBlank())
                options.add(txt);
        }

        if (options.size() < 2) {
            ToastManager.showToast(this, "Please enter at least 2 valid options", ToastManager.ToastType.WARNING);
            return;
        }

        btn.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                PollService.getInstance().createPollMessage(conversationId, question, options,
                        multipleCheck.isSelected(), anonymousCheck.isSelected()).get();
                return null;
            }

            @Override
            protected void done() {
                btn.setEnabled(true);
                try {
                    get();
                    ToastManager.showToast(CreatePollDialog.this, "Poll published!", ToastManager.ToastType.SUCCESS);
                    dispose();
                    if (onSuccessCallback != null)
                        onSuccessCallback.run();
                } catch (Exception ex) {
                    ToastManager.showToast(CreatePollDialog.this, "Failed to publish poll",
                            ToastManager.ToastType.ERROR);
                }
            }
        }.execute();
    }
}
