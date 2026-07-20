package ui;

import controller.GameController;
import controller.GameController.ViewCallback;
import model.Difficulty;
import model.HintType;
import sound.SoundManager;
import ui.components.AnimatedProgressBar;
import ui.components.RoundedButton;
import ui.components.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Primary gameplay panel for Smart Number Guessing Game Pro.
 *
 * Layout (BorderLayout):
 *   NORTH  — gradient header with player info, timer, difficulty badge
 *   CENTER — main game area (feedback label, input, guess button, progress bar)
 *   EAST   — sidebar: guess history + hints panel
 *   SOUTH  — control bar (New Game, Restart, Difficulty radio buttons)
 *
 * Implements {@link ViewCallback} to receive events from {@link GameController}.
 */
public class GamePanel extends JPanel implements ViewCallback {

    // ── Dependencies ──────────────────────────────────────────────────────────
    private final GameController gc = GameController.getInstance();
    private final ThemeManager   tm = ThemeManager.getInstance();

    // ── UI Components ─────────────────────────────────────────────────────────
    private JLabel              headerTitle;
    private JLabel              timerLabel;
    private JLabel              difficultyBadge;
    private JLabel              feedbackLabel;
    private JLabel              feedbackEmoji;
    private JLabel              attemptsLabel;
    private JLabel              scoreLabel;
    private JTextField          guessField;
    private RoundedButton       guessBtn;
    private AnimatedProgressBar progressBar;
    private JLabel              rangeLabel;

    // Sidebar
    private JPanel              historyPanel;
    private JPanel              hintsPanel;
    private final List<String>  guessHistory = new ArrayList<>();
    private final List<String>  hints        = new ArrayList<>();

    // Win overlay reference (for confetti)
    private JLayeredPane        layeredPane;

    // ── Constructor ───────────────────────────────────────────────────────────

    public GamePanel(JLayeredPane layeredPane) {
        this.layeredPane = layeredPane;
        gc.addView(this);
        buildUI();
        registerKeyBindings();
        tm.addChangeListener(this::applyTheme);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // UI BUILD
    // ═════════════════════════════════════════════════════════════════════════

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(tm.bgPrimary());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildSidebar(), BorderLayout.EAST);
        add(buildSouth(),   BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, tm.gradientStart(),
                    getWidth(), 0, tm.gradientEnd()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom separator
                g2.setColor(tm.border());
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setLayout(new BorderLayout(12, 0));
        header.setBorder(new EmptyBorder(14, 24, 14, 24));
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 72));

        // Left: logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel logo = new JLabel("🎯");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        headerTitle = new JLabel("Smart Number Guessing Game Pro");
        headerTitle.setFont(ThemeManager.FONT_TITLE);
        headerTitle.setForeground(tm.textPrimary());
        left.add(logo);
        left.add(headerTitle);
        header.add(left, BorderLayout.WEST);

        // Center: difficulty badge
        difficultyBadge = new JLabel();
        difficultyBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        difficultyBadge.setOpaque(true);
        difficultyBadge.setBorder(new EmptyBorder(4, 12, 4, 12));
        difficultyBadge.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(difficultyBadge, BorderLayout.CENTER);

        // Right: timer
        timerLabel = new JLabel("⏱ 0:00");
        timerLabel.setFont(ThemeManager.FONT_MONO_SM);
        timerLabel.setForeground(tm.accent());
        header.add(timerLabel, BorderLayout.EAST);

        return header;
    }

    // ── Center ────────────────────────────────────────────────────────────────

    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(tm.bgPrimary());
        center.setBorder(new EmptyBorder(30, 60, 20, 40));

        // Range label
        rangeLabel = new JLabel("Guess a number between 1 and 100");
        rangeLabel.setFont(ThemeManager.FONT_BODY);
        rangeLabel.setForeground(tm.textMuted());
        rangeLabel.setAlignmentX(CENTER_ALIGNMENT);
        center.add(rangeLabel);
        center.add(Box.createVerticalStrut(28));

        // Feedback emoji (large)
        feedbackEmoji = new JLabel("🤔", SwingConstants.CENTER);
        feedbackEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        feedbackEmoji.setAlignmentX(CENTER_ALIGNMENT);
        center.add(feedbackEmoji);
        center.add(Box.createVerticalStrut(12));

        // Feedback message
        feedbackLabel = new JLabel("Enter your first guess!", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        feedbackLabel.setForeground(tm.textPrimary());
        feedbackLabel.setAlignmentX(CENTER_ALIGNMENT);
        center.add(feedbackLabel);
        center.add(Box.createVerticalStrut(28));

        // Input row
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        inputRow.setOpaque(false);
        inputRow.setAlignmentX(CENTER_ALIGNMENT);

        guessField = new JTextField(8);
        guessField.setFont(ThemeManager.FONT_MONO_LG);
        guessField.setBackground(tm.inputBg());
        guessField.setForeground(tm.accent());
        guessField.setCaretColor(tm.accent());
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setPreferredSize(new Dimension(160, 60));
        guessField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(tm.border(), 2, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        guessField.addActionListener(e -> submitGuess());

        guessBtn = new RoundedButton("Guess →", RoundedButton.Variant.PRIMARY);
        guessBtn.setPreferredSize(new Dimension(130, 60));
        guessBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        guessBtn.addActionListener(e -> submitGuess());

        inputRow.add(guessField);
        inputRow.add(guessBtn);
        center.add(inputRow);
        center.add(Box.createVerticalStrut(28));

        // Attempts + score row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 32, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(CENTER_ALIGNMENT);

        attemptsLabel = statChip("Attempts", "0 / 10", tm.warning());
        scoreLabel    = statChip("Score", "—", tm.accent());
        statsRow.add(attemptsLabel.getParent());
        statsRow.add(scoreLabel.getParent());
        center.add(statsRow);
        center.add(Box.createVerticalStrut(20));

        // Progress bar
        progressBar = new AnimatedProgressBar();
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        center.add(progressBar);

        return center;
    }

    /** Creates a labelled stat chip, returns the value label. */
    private JLabel statChip(String labelText, String value, Color accent) {
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBackground(tm.bgCard());
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(tm.border(), 1, true),
            new EmptyBorder(8, 20, 8, 20)
        ));

        JLabel lbl = new JLabel(labelText, SwingConstants.CENTER);
        lbl.setFont(ThemeManager.FONT_SMALL);
        lbl.setForeground(tm.textMuted());
        lbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(accent);
        val.setAlignmentX(CENTER_ALIGNMENT);

        chip.add(lbl);
        chip.add(val);
        return val;  // caller holds reference to update later
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(tm.bgSecondary());
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, tm.border()),
            new EmptyBorder(16, 12, 16, 12)
        ));
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Guess History
        JLabel histTitle = new JLabel("📋 Guess History");
        histTitle.setFont(ThemeManager.FONT_TITLE);
        histTitle.setForeground(tm.textPrimary());
        histTitle.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(histTitle);
        sidebar.add(Box.createVerticalStrut(8));

        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBackground(tm.bgSecondary());
        historyPanel.setAlignmentX(LEFT_ALIGNMENT);
        JScrollPane histScroll = new JScrollPane(historyPanel);
        histScroll.setBorder(BorderFactory.createLineBorder(tm.border(), 1));
        histScroll.setPreferredSize(new Dimension(196, 200));
        histScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        histScroll.getViewport().setBackground(tm.bgSecondary());
        sidebar.add(histScroll);
        sidebar.add(Box.createVerticalStrut(20));

        // Hints
        JLabel hintsTitle = new JLabel("💡 Hints");
        hintsTitle.setFont(ThemeManager.FONT_TITLE);
        hintsTitle.setForeground(tm.textPrimary());
        hintsTitle.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(hintsTitle);
        sidebar.add(Box.createVerticalStrut(8));

        hintsPanel = new JPanel();
        hintsPanel.setLayout(new BoxLayout(hintsPanel, BoxLayout.Y_AXIS));
        hintsPanel.setBackground(tm.bgSecondary());
        hintsPanel.setAlignmentX(LEFT_ALIGNMENT);
        JScrollPane hintScroll = new JScrollPane(hintsPanel);
        hintScroll.setBorder(BorderFactory.createLineBorder(tm.border(), 1));
        hintScroll.setPreferredSize(new Dimension(196, 160));
        hintScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        hintScroll.getViewport().setBackground(tm.bgSecondary());
        sidebar.add(hintScroll);

        // Hint tip
        JLabel hintTip = new JLabel("<html><i>Hints unlock after 3<br>wrong guesses.</i></html>");
        hintTip.setFont(ThemeManager.FONT_SMALL);
        hintTip.setForeground(tm.textMuted());
        hintTip.setAlignmentX(LEFT_ALIGNMENT);
        hintTip.setBorder(new EmptyBorder(8, 0, 0, 0));
        sidebar.add(hintTip);

        return sidebar;
    }

    // ── South control bar ─────────────────────────────────────────────────────

    private JPanel buildSouth() {
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(tm.bgCard());
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, tm.border()),
            new EmptyBorder(10, 20, 10, 20)
        ));

        // Difficulty radio buttons
        JPanel diffRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        diffRow.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        JLabel diffLbl = new JLabel("Difficulty:");
        diffLbl.setFont(ThemeManager.FONT_BODY);
        diffLbl.setForeground(tm.textMuted());
        diffRow.add(diffLbl);
        for (Difficulty d : Difficulty.values()) {
            JRadioButton rb = new JRadioButton(d.getDisplayName());
            rb.setFont(ThemeManager.FONT_SMALL);
            rb.setForeground(tm.textPrimary());
            rb.setOpaque(false);
            rb.setFocusPainted(false);
            rb.setSelected(d == gc.getDifficulty());
            rb.addActionListener(e -> {
                gc.setDifficulty(d);
                gc.startNewGame();
                SoundManager.getInstance().play(SoundManager.Sound.CLICK);
            });
            bg.add(rb);
            diffRow.add(rb);
        }
        south.add(diffRow, BorderLayout.WEST);

        // Action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        RoundedButton newGameBtn  = new RoundedButton("🆕 New Game",  RoundedButton.Variant.PRIMARY);
        RoundedButton restartBtn  = new RoundedButton("🔄 Restart",   RoundedButton.Variant.SECONDARY);

        newGameBtn.setPreferredSize(new Dimension(130, 36));
        restartBtn.setPreferredSize(new Dimension(120, 36));

        newGameBtn.addActionListener(e -> {
            gc.startNewGame();
            SoundManager.getInstance().play(SoundManager.Sound.CLICK);
        });
        restartBtn.addActionListener(e -> {
            gc.restartGame();
            SoundManager.getInstance().play(SoundManager.Sound.CLICK);
        });

        btnRow.add(restartBtn);
        btnRow.add(newGameBtn);
        south.add(btnRow, BorderLayout.EAST);

        return south;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // KEYBOARD SHORTCUTS
    // ═════════════════════════════════════════════════════════════════════════

    private void registerKeyBindings() {
        InputMap im  = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // Ctrl+N — New Game
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newGame");
        am.put("newGame", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { gc.startNewGame(); }
        });

        // Ctrl+R — Restart
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "restart");
        am.put("restart", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { gc.restartGame(); }
        });
    }

    // ═════════════════════════════════════════════════════════════════════════
    // GAME ACTIONS
    // ═════════════════════════════════════════════════════════════════════════

    private void submitGuess() {
        if (!gc.isGameActive()) {
            setFeedback("🆕", "Start a new game first!", tm.textMuted(), false);
            return;
        }
        gc.submitGuess(guessField.getText().trim());
        guessField.setText("");
        guessField.requestFocus();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // VIEW CALLBACK IMPLEMENTATION
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void onGameStarted(Difficulty difficulty) {
        SwingUtilities.invokeLater(() -> {
            guessHistory.clear();
            hints.clear();
            historyPanel.removeAll();
            hintsPanel.removeAll();
            historyPanel.revalidate();
            hintsPanel.revalidate();

            progressBar.reset();
            attemptsLabel.setText("0 / " + difficulty.getMaxAttempts());
            scoreLabel.setText("—");
            timerLabel.setText("⏱ 0:00");

            rangeLabel.setText("Guess a number between "
                + difficulty.getMinValue() + " and " + difficulty.getMaxValue());

            difficultyBadge.setText("  " + difficulty.getDisplayName()
                + "  |  " + difficulty.getRangeLabel() + "  ");
            difficultyBadge.setBackground(diffColor(difficulty));
            difficultyBadge.setForeground(Color.WHITE);

            setFeedback("🤔", "Enter your first guess!", tm.textPrimary(), false);
            guessField.setEnabled(true);
            guessBtn.setEnabled(true);
            guessField.requestFocus();
        });
    }

    @Override
    public void onTooLow(int guess, int attemptsLeft) {
        SwingUtilities.invokeLater(() -> {
            setFeedback("📉", "Too Low! Try higher.", tm.warning(), true);
            addHistory("↑ " + guess + " — Too Low", tm.warning());
            updateAttempts();
        });
    }

    @Override
    public void onTooHigh(int guess, int attemptsLeft) {
        SwingUtilities.invokeLater(() -> {
            setFeedback("📈", "Too High! Try lower.", tm.warning(), true);
            addHistory("↓ " + guess + " — Too High", tm.warning());
            updateAttempts();
        });
    }

    @Override
    public void onCorrectGuess(int score, int attempts, long elapsedSec) {
        SwingUtilities.invokeLater(() -> {
            setFeedback("🎉", "Correct! You got it in " + attempts + " attempts!", tm.success(), false);
            addHistory("✅ Correct in " + attempts + " attempts!", tm.success());
            updateAttempts();
            scoreLabel.setText(String.valueOf(score));
            progressBar.setProgress(1.0);
            guessField.setEnabled(false);
            guessBtn.setEnabled(false);
            launchConfetti();
        });
    }

    @Override
    public void onGameOver(int secretNumber) {
        SwingUtilities.invokeLater(() -> {
            setFeedback("💀", "Game Over! The number was " + secretNumber + ".", tm.danger(), false);
            addHistory("❌ Game Over — was " + secretNumber, tm.danger());
            updateAttempts();
            guessField.setEnabled(false);
            guessBtn.setEnabled(false);
        });
    }

    @Override
    public void onHintRevealed(HintType type, String hintText) {
        SwingUtilities.invokeLater(() -> addHint(type.getLabel(), hintText));
    }

    @Override
    public void onTimerTick(long elapsedSeconds) {
        SwingUtilities.invokeLater(() -> {
            long m = elapsedSeconds / 60, s = elapsedSeconds % 60;
            timerLabel.setText(String.format("⏱ %d:%02d", m, s));
            progressBar.setProgress(gc.getProgressFraction());
        });
    }

    @Override
    public void onInputError(String message) {
        SwingUtilities.invokeLater(() -> {
            setFeedback("⚠️", message, tm.danger(), false);
            guessField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(tm.danger(), 2, true),
                new EmptyBorder(4, 8, 4, 8)));
            // Reset border after 1.5s
            new Timer(1500, e -> guessField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(tm.border(), 2, true),
                new EmptyBorder(4, 8, 4, 8)))){{ setRepeats(false); start(); }};
        });
    }

    // ═════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    private void setFeedback(String emoji, String message, Color color, boolean shake) {
        feedbackEmoji.setText(emoji);
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(color);
        if (shake) shakeComponent(feedbackLabel);
    }

    private void updateAttempts() {
        attemptsLabel.setText(gc.getAttemptsUsed() + " / " + gc.getMaxAttempts());
    }

    private void addHistory(String entry, Color color) {
        JLabel lbl = new JLabel("  " + entry);
        lbl.setFont(ThemeManager.FONT_SMALL);
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, tm.border()));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        historyPanel.add(lbl, 0);  // newest at top
        historyPanel.revalidate();
        historyPanel.repaint();
    }

    private void addHint(String type, String text) {
        JPanel hp = new JPanel(new BorderLayout());
        hp.setOpaque(false);
        hp.setBorder(new EmptyBorder(4, 4, 4, 4));
        hp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel typeLbl = new JLabel("💡 " + type);
        typeLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        typeLbl.setForeground(tm.warning());

        JLabel textLbl = new JLabel("<html>" + text + "</html>");
        textLbl.setFont(ThemeManager.FONT_SMALL);
        textLbl.setForeground(tm.textPrimary());

        hp.add(typeLbl, BorderLayout.NORTH);
        hp.add(textLbl, BorderLayout.CENTER);
        hintsPanel.add(hp);
        hintsPanel.revalidate();
        hintsPanel.repaint();
    }

    /** Horizontal shake animation for visual feedback on error/result. */
    private void shakeComponent(JComponent comp) {
        int[] offsets = {-8, 8, -6, 6, -4, 4, -2, 2, 0};
        Timer shake = new Timer(30, null);
        int[] idx = {0};
        Point orig = comp.getLocation();
        shake.addActionListener(e -> {
            if (idx[0] >= offsets.length) {
                comp.setLocation(orig);
                shake.stop();
                return;
            }
            comp.setLocation(orig.x + offsets[idx[0]], orig.y);
            idx[0]++;
        });
        shake.start();
    }

    private void launchConfetti() {
        if (layeredPane == null) return;
        ConfettiPanel cp = new ConfettiPanel(layeredPane);
        cp.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
        layeredPane.add(cp, JLayeredPane.POPUP_LAYER);
        cp.start();
        layeredPane.repaint();
    }

    private Color diffColor(Difficulty d) {
        return switch (d) {
            case EASY   -> new Color(0x1A7F37);
            case MEDIUM -> new Color(0x0969DA);
            case HARD   -> new Color(0xBF4700);
            case EXPERT -> new Color(0xCF222E);
        };
    }

    private void applyTheme() {
        setBackground(tm.bgPrimary());
        repaint();
        revalidate();
    }
}
