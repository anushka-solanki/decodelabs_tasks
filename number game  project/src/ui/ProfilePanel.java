package ui;

import controller.ProfileController;
import model.Player;
import ui.components.RoundedButton;
import ui.components.ThemeManager;
import utils.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Player profile setup panel.
 * Allows the player to set their name and choose an avatar emoji.
 * Shown on first launch and accessible from Settings.
 */
public class ProfilePanel extends JPanel {

    private final ProfileController ctrl = ProfileController.getInstance();
    private JTextField              nameField;
    private JLabel                  avatarPreview;
    private int                     selectedAvatarIdx = 0;
    private Runnable                onSave;

    // ── Constructor ───────────────────────────────────────────────────────────

    public ProfilePanel() {
        buildUI();
        ThemeManager.getInstance().addChangeListener(this::applyTheme);
    }

    public void setOnSave(Runnable r) { this.onSave = r; }

    // ── Build UI ──────────────────────────────────────────────────────────────

    private void buildUI() {
        ThemeManager tm = ThemeManager.getInstance();
        setLayout(new GridBagLayout());
        setBackground(tm.bgPrimary());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(tm.bgCard());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(tm.border(), 1, true),
            new EmptyBorder(32, 48, 32, 48)
        ));

        // ── Title ─────────────────────────────────────────────────────────────
        JLabel title = new JLabel("👤  Player Profile", SwingConstants.CENTER);
        title.setFont(ThemeManager.FONT_HEADER);
        title.setForeground(tm.accent());
        title.setAlignmentX(CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(24));

        // ── Avatar picker ─────────────────────────────────────────────────────
        JLabel avatarLabel = new JLabel("Choose Avatar:");
        avatarLabel.setFont(ThemeManager.FONT_BODY);
        avatarLabel.setForeground(tm.textMuted());
        avatarLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(avatarLabel);
        card.add(Box.createVerticalStrut(8));

        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        avatarRow.setOpaque(false);
        avatarRow.setAlignmentX(LEFT_ALIGNMENT);

        for (int i = 0; i < Player.AVATARS.length; i++) {
            final int idx = i;
            JButton btn = new JButton(Player.AVATARS[i]);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            btn.setPreferredSize(new Dimension(44, 44));
            btn.setFocusPainted(false);
            btn.setBorderPainted(true);
            btn.setBackground(tm.bgSecondary());
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> selectAvatar(idx, btn));
            avatarRow.add(btn);
        }

        card.add(avatarRow);
        card.add(Box.createVerticalStrut(20));

        // Avatar preview
        avatarPreview = new JLabel(ctrl.getPlayer().getAvatarEmoji(), SwingConstants.CENTER);
        avatarPreview.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        avatarPreview.setAlignmentX(CENTER_ALIGNMENT);
        card.add(avatarPreview);
        card.add(Box.createVerticalStrut(20));

        // ── Name input ────────────────────────────────────────────────────────
        JLabel nameLbl = new JLabel("Player Name:");
        nameLbl.setFont(ThemeManager.FONT_BODY);
        nameLbl.setForeground(tm.textMuted());
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(6));

        nameField = new JTextField(ctrl.getPlayer().getName());
        nameField.setFont(ThemeManager.FONT_MONO_SM);
        nameField.setBackground(tm.inputBg());
        nameField.setForeground(tm.textPrimary());
        nameField.setCaretColor(tm.accent());
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(tm.border(), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nameField.setAlignmentX(LEFT_ALIGNMENT);
        card.add(nameField);
        card.add(Box.createVerticalStrut(24));

        // ── Save button ───────────────────────────────────────────────────────
        RoundedButton saveBtn = new RoundedButton("💾  Save Profile", RoundedButton.Variant.SUCCESS);
        saveBtn.setPreferredSize(new Dimension(200, 42));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> saveProfile());
        card.add(saveBtn);

        add(card);
        setSelectedAvatarIndex(findAvatarIndex(ctrl.getPlayer().getAvatarEmoji()));
    }

    // ── Logic ─────────────────────────────────────────────────────────────────

    private void selectAvatar(int idx, JButton btn) {
        selectedAvatarIdx = idx;
        avatarPreview.setText(Player.AVATARS[idx]);
        // Highlight selected
        ThemeManager tm = ThemeManager.getInstance();
        Container parent = btn.getParent();
        for (Component c : parent.getComponents()) {
            if (c instanceof JButton b) {
                b.setBackground(tm.bgSecondary());
                b.setBorder(UIManager.getBorder("Button.border"));
            }
        }
        btn.setBackground(tm.accent());
        btn.setBorder(BorderFactory.createLineBorder(tm.accent(), 2, true));
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        var result  = Validator.validatePlayerName(name);
        if (!result.valid) {
            JOptionPane.showMessageDialog(this,
                result.errorMessage, "Invalid Name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ctrl.updateName(name);
        ctrl.updateAvatar(Player.AVATARS[selectedAvatarIdx]);
        JOptionPane.showMessageDialog(this,
            "Profile saved! Welcome, " + name + "!", "Saved",
            JOptionPane.INFORMATION_MESSAGE);
        if (onSave != null) onSave.run();
    }

    private int findAvatarIndex(String emoji) {
        for (int i = 0; i < Player.AVATARS.length; i++) {
            if (Player.AVATARS[i].equals(emoji)) return i;
        }
        return 0;
    }

    private void setSelectedAvatarIndex(int idx) {
        selectedAvatarIdx = idx;
    }

    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBackground(tm.bgPrimary());
        repaint();
    }
}
