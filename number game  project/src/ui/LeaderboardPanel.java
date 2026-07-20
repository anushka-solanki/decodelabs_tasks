package ui;

import controller.LeaderboardController;
import controller.LeaderboardController.LeaderboardEntry;
import ui.components.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Leaderboard panel displaying the top scores sorted by score (descending).
 * Uses a custom-rendered JTable with alternating row colours and medal icons.
 */
public class LeaderboardPanel extends JPanel {

    private final LeaderboardController lb = LeaderboardController.getInstance();
    private DefaultTableModel           tableModel;
    private JTable                      table;

    // ── Constructor ───────────────────────────────────────────────────────────

    public LeaderboardPanel() {
        buildUI();
        ThemeManager.getInstance().addChangeListener(this::applyTheme);
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    private void buildUI() {
        ThemeManager tm = ThemeManager.getInstance();
        setLayout(new BorderLayout(0, 0));
        setBackground(tm.bgPrimary());

        // Header
        JPanel header = buildHeader(tm);
        add(header, BorderLayout.NORTH);

        // Table
        buildTable(tm);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(tm.bgSecondary());
        scroll.setBackground(tm.bgSecondary());
        add(scroll, BorderLayout.CENTER);

        // Refresh button bar
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        footer.setBackground(tm.bgCard());
        JButton refresh = new JButton("🔄 Refresh");
        refresh.setFont(ThemeManager.FONT_BTN);
        refresh.setBackground(tm.accent());
        refresh.setForeground(Color.WHITE);
        refresh.setFocusPainted(false);
        refresh.setBorderPainted(false);
        refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refresh.addActionListener(e -> { lb.reload(); populateTable(); });
        footer.add(refresh);
        add(footer, BorderLayout.SOUTH);

        populateTable();
    }

    private JPanel buildHeader(ThemeManager tm) {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                    0, 0, tm.gradientStart(),
                    getWidth(), getHeight(), tm.gradientEnd()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        header.setPreferredSize(new Dimension(0, 80));

        JLabel title = new JLabel("🏆  Leaderboard");
        title.setFont(ThemeManager.FONT_HEADER);
        title.setForeground(tm.textPrimary());
        header.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("Top " + utils.Constants.MAX_LEADERBOARD_ROWS + " Players");
        sub.setFont(ThemeManager.FONT_BODY);
        sub.setForeground(tm.textMuted());
        header.add(sub, BorderLayout.EAST);

        return header;
    }

    private void buildTable(ThemeManager tm) {
        String[] cols = {"#", "Player", "Score", "Difficulty", "Attempts", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setBackground(tm.bgSecondary());
        table.setForeground(tm.textPrimary());
        table.setFont(ThemeManager.FONT_BODY);
        table.setRowHeight(38);
        table.setGridColor(tm.border());
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(tm.accent());
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFocusable(false);

        // Header style
        JTableHeader th = table.getTableHeader();
        th.setBackground(tm.bgCard());
        th.setForeground(tm.textMuted());
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, tm.border()));
        th.setReorderingAllowed(false);
        th.setResizingAllowed(true);

        // Column widths
        int[] widths = {40, 130, 90, 110, 90, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Custom renderer for alternating rows + medal emojis
        TableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                setHorizontalAlignment(col == 0 ? CENTER : col == 2 ? RIGHT : LEFT);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!selected) {
                    setBackground(row % 2 == 0 ? tm.bgSecondary() : tm.bgCard());
                    setForeground(tm.textPrimary());
                }
                // Medal for top 3
                if (col == 0) {
                    int rank = row + 1;
                    setText(rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉"
                            : String.valueOf(rank));
                    setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                } else {
                    setFont(col == 2 ? new Font("Segoe UI", Font.BOLD, 14)
                                     : ThemeManager.FONT_BODY);
                }
                // Highlight score in accent color
                if (col == 2 && !selected) setForeground(tm.accent());
                return this;
            }
        };
        for (int i = 0; i < cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    public void populateTable() {
        tableModel.setRowCount(0);
        List<LeaderboardEntry> entries = lb.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry e = entries.get(i);
            tableModel.addRow(new Object[]{
                i + 1,
                e.player(),
                e.score(),
                e.difficulty(),
                e.attempts(),
                e.date()
            });
        }
        if (entries.isEmpty()) {
            tableModel.addRow(new Object[]{"—", "No records yet", "—", "—", "—", "—"});
        }
    }

    // ── Theme ─────────────────────────────────────────────────────────────────

    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBackground(tm.bgPrimary());
        table.setBackground(tm.bgSecondary());
        table.setForeground(tm.textPrimary());
        table.getTableHeader().setBackground(tm.bgCard());
        table.getTableHeader().setForeground(tm.textMuted());
        repaint();
    }
}
