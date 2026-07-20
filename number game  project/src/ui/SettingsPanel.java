package ui;

import controller.GameController;
import model.Difficulty;
import sound.SoundManager;
import ui.components.RoundedButton;
import ui.components.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Settings panel for theme, sound, and difficulty configuration.
 * Changes take effect immediately.
 */
public class SettingsPanel extends JPanel {

    private final GameController  gc = GameController.getInstance();
    private final ThemeManager    tm = ThemeManager.getInstance();
    private final SoundManager    sm = SoundManager.getInstance();

    private JToggleButton         themeToggle;
    private JToggleButton         soundToggle;
    private JComboBox<Difficulty> diffCombo;

    // ── Constructor ───────────────────────────────────────────────────────────

    public SettingsPanel() {
        buildUI();
        tm.addChangeListener(this::applyTheme);
    }

    // ── Build UI ──────────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new GridBagLayout());
        setBackground(tm.bgPrimary());

        JPanel card = buildCard();
        add(card);
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(tm.bgCard());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(tm.border(), 1, true),
            new EmptyBorder(32, 48, 32, 48)
        ));

        // Title
        JLabel title = new JLabel("⚙️  Settings", SwingConstants.CENTER);
        title.setFont(ThemeManager.FONT_HEADER);
        title.setForeground(tm.accent());
        title.setAlignmentX(CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(32));

        // Section: Appearance
        card.add(sectionLabel("🎨  Appearance"));
        card.add(Box.createVerticalStrut(10));
        card.add(buildThemeRow());
        card.add(Box.createVerticalStrut(28));

        // Section: Audio
        card.add(sectionLabel("🔊  Audio"));
        card.add(Box.createVerticalStrut(10));
        card.add(buildSoundRow());
        card.add(Box.createVerticalStrut(28));

        // Section: Gameplay
        card.add(sectionLabel("🎮  Gameplay"));
        card.add(Box.createVerticalStrut(10));
        card.add(buildDifficultyRow());
        card.add(Box.createVerticalStrut(32));

        // Apply button
        RoundedButton applyBtn = new RoundedButton("✅  Apply & New Game", RoundedButton.Variant.SUCCESS);
        applyBtn.setPreferredSize(new Dimension(240, 42));
        applyBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        applyBtn.setAlignmentX(CENTER_ALIGNMENT);
        applyBtn.addActionListener(e -> applyAndStart());
        card.add(applyBtn);

        return card;
    }

    // ── Row builders ──────────────────────────────────────────────────────────

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeManager.FONT_TITLE);
        lbl.setForeground(tm.textPrimary());
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildThemeRow() {
        JPanel row = rowPanel();

        JLabel lbl = rowLabel("Dark / Light Mode:");
        themeToggle = new JToggleButton(tm.isDark() ? "🌙 Dark" : "☀️ Light");
        themeToggle.setSelected(tm.isDark());
        styleToggle(themeToggle);
        themeToggle.addActionListener(e -> {
            tm.toggleTheme();
            themeToggle.setText(tm.isDark() ? "🌙 Dark" : "☀️ Light");
            sm.play(SoundManager.Sound.CLICK);
        });

        row.add(lbl);
        row.add(themeToggle);
        return row;
    }

    private JPanel buildSoundRow() {
        JPanel row = rowPanel();

        JLabel lbl = rowLabel("Sound Effects:");
        soundToggle = new JToggleButton(sm.isSoundEnabled() ? "🔊 On" : "🔇 Off");
        soundToggle.setSelected(sm.isSoundEnabled());
        styleToggle(soundToggle);
        soundToggle.addActionListener(e -> {
            boolean enabled = soundToggle.isSelected();
            sm.setSoundEnabled(enabled);
            soundToggle.setText(enabled ? "🔊 On" : "🔇 Off");
            if (enabled) sm.play(SoundManager.Sound.CLICK);
        });

        row.add(lbl);
        row.add(soundToggle);
        return row;
    }

    private JPanel buildDifficultyRow() {
        JPanel row = rowPanel();

        JLabel lbl = rowLabel("Difficulty Level:");
        diffCombo = new JComboBox<>(Difficulty.values());
        diffCombo.setSelectedItem(gc.getDifficulty());
        diffCombo.setFont(ThemeManager.FONT_BODY);
        diffCombo.setBackground(tm.inputBg());
        diffCombo.setForeground(tm.textPrimary());
        diffCombo.setPreferredSize(new Dimension(180, 36));
        diffCombo.setMaximumSize(new Dimension(220, 36));
        diffCombo.addActionListener(e -> {
            Difficulty d = (Difficulty) diffCombo.getSelectedItem();
            if (d != null) gc.setDifficulty(d);
        });

        row.add(lbl);
        row.add(diffCombo);
        return row;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPanel rowPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private JLabel rowLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_BODY);
        l.setForeground(tm.textMuted());
        l.setPreferredSize(new Dimension(180, 30));
        return l;
    }

    private void styleToggle(JToggleButton btn) {
        btn.setFont(ThemeManager.FONT_BTN);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 34));
        btn.setBackground(tm.accent());
        btn.setForeground(Color.WHITE);
    }

    private void applyAndStart() {
        Difficulty d = (Difficulty) diffCombo.getSelectedItem();
        if (d != null) gc.setDifficulty(d);
        sm.play(SoundManager.Sound.CLICK);
        gc.startNewGame();
    }

    // ── Theme refresh ─────────────────────────────────────────────────────────

    private void applyTheme() {
        setBackground(tm.bgPrimary());
        repaint();
        revalidate();
    }
}
