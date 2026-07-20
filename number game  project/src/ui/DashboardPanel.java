package ui;

import controller.LeaderboardController;
import controller.ProfileController;
import model.Player;
import ui.components.GraphPanel;
import ui.components.ThemeManager;
import utils.CSVExporter;
import utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Analytics dashboard showing player lifetime statistics, performance KPIs,
 * and a bar chart of recent scores loaded from CSV history.
 */
public class DashboardPanel extends JPanel {

    private final ProfileController pc = ProfileController.getInstance();
    private final LeaderboardController lb = LeaderboardController.getInstance();
    private final ThemeManager tm = ThemeManager.getInstance();
    private GraphPanel graphPanel;

    // ── Stat value labels (refreshed on each visit) ───────────────────────────
    private JLabel lblGamesPlayed, lblWins, lblLosses, lblWinPct;
    private JLabel lblBestScore,   lblFastest, lblAvgAttempts, lblAccuracy;

    // ── Constructor ───────────────────────────────────────────────────────────

    public DashboardPanel() {
        buildUI();
        tm.addChangeListener(this::applyTheme);
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(tm.bgPrimary());

        // Header
        add(buildHeader(), BorderLayout.NORTH);

        // Content scroll area
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(tm.bgPrimary());
        content.setBorder(new EmptyBorder(16, 24, 16, 24));

        // KPI grid
        content.add(buildKpiGrid());
        content.add(Box.createVerticalStrut(24));

        // Score history chart
        content.add(buildChartSection());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(tm.bgPrimary());
        add(scroll, BorderLayout.CENTER);

        refreshStats();
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, tm.gradientStart(),
                    getWidth(), getHeight(), tm.gradientEnd()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setLayout(new BorderLayout(16, 0));
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        header.setPreferredSize(new Dimension(0, 90));

        Player p = pc.getPlayer();

        JLabel avatar = new JLabel(p.getAvatarEmoji());
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));

        JPanel textCol = new JPanel();
        textCol.setOpaque(false);
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(p.getName() + "'s Dashboard");
        name.setFont(ThemeManager.FONT_HEADER);
        name.setForeground(tm.textPrimary());

        JLabel sub = new JLabel("📊  Lifetime Statistics & Performance");
        sub.setFont(ThemeManager.FONT_BODY);
        sub.setForeground(tm.textMuted());

        textCol.add(name);
        textCol.add(Box.createVerticalStrut(4));
        textCol.add(sub);

        header.add(avatar, BorderLayout.WEST);
        header.add(textCol, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("🔄");
        refreshBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.setToolTipText("Refresh Stats");
        refreshBtn.addActionListener(e -> refreshStats());
        header.add(refreshBtn, BorderLayout.EAST);

        return header;
    }

    // ── KPI Grid ──────────────────────────────────────────────────────────────

    private JPanel buildKpiGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 4, 14, 14));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);

        lblGamesPlayed = new JLabel("0");
        lblWins        = new JLabel("0");
        lblLosses      = new JLabel("0");
        lblWinPct      = new JLabel("0%");
        lblBestScore   = new JLabel("0");
        lblFastest     = new JLabel("N/A");
        lblAvgAttempts = new JLabel("0");
        lblAccuracy    = new JLabel("0%");

        grid.add(kpiCard("🎮 Games Played",  lblGamesPlayed, tm.accent()));
        grid.add(kpiCard("✅ Total Wins",     lblWins,        tm.success()));
        grid.add(kpiCard("❌ Total Losses",   lblLosses,      tm.danger()));
        grid.add(kpiCard("📈 Win Rate",       lblWinPct,      tm.accent2()));
        grid.add(kpiCard("🏆 Best Score",     lblBestScore,   new Color(0xFFD700)));
        grid.add(kpiCard("⚡ Fastest Win",    lblFastest,     tm.warning()));
        grid.add(kpiCard("🎯 Avg Attempts",   lblAvgAttempts, tm.textMuted()));
        grid.add(kpiCard("💯 Accuracy",       lblAccuracy,    tm.success()));

        return grid;
    }

    private JPanel kpiCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tm.bgCard());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Accent top border
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeManager.FONT_SMALL);
        lbl.setForeground(tm.textMuted());
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    // ── Score History Chart ───────────────────────────────────────────────────

    private JPanel buildChartSection() {
        JPanel section = new JPanel(new BorderLayout(0, 12));
        section.setOpaque(false);
        section.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel("📉  Recent Score History");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(tm.textPrimary());
        section.add(title, BorderLayout.NORTH);

        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(600, 200));
        section.add(graphPanel, BorderLayout.CENTER);

        return section;
    }

    // ── Data Refresh ──────────────────────────────────────────────────────────

    /** Reloads all stats from the player profile and history CSV. */
    public void refreshStats() {
        Player p = pc.getPlayer();

        lblGamesPlayed.setText(String.valueOf(p.getTotalGamesPlayed()));
        lblWins.setText(String.valueOf(p.getTotalWins()));
        lblLosses.setText(String.valueOf(p.getTotalLosses()));
        lblWinPct.setText(String.format("%.1f%%", p.getWinPercentage()));
        lblBestScore.setText(String.valueOf(p.getBestScore()));
        lblFastest.setText(p.getFastestWinFormatted());
        lblAvgAttempts.setText(String.format("%.1f", p.getAverageAttempts()));
        lblAccuracy.setText(String.format("%.1f%%", p.getAccuracyPercent()));

        loadChartData();
        repaint();
    }

    private void loadChartData() {
        List<String> rows = CSVExporter.readDataRows(Constants.HISTORY_FILE);
        List<Integer> scores = new ArrayList<>();
        List<String>  labels = new ArrayList<>();

        // Read last 10 won games
        int count = 0;
        for (int i = rows.size() - 1; i >= 0 && count < 10; i--) {
            String[] cols = rows.get(i).split(",", -1);
            if (cols.length >= 9 && "Yes".equals(cols[7])) {
                try {
                    scores.add(0, Integer.parseInt(cols[6]));
                    labels.add(0, "G" + (rows.size() - i));
                    count++;
                } catch (NumberFormatException ignored) {}
            }
        }
        graphPanel.setData(scores, labels);
    }

    // ── Theme ─────────────────────────────────────────────────────────────────

    private void applyTheme() {
        setBackground(tm.bgPrimary());
        repaint();
        revalidate();
    }
}
