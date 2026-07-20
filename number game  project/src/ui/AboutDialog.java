package ui;

import ui.components.RoundedButton;
import ui.components.ThemeManager;
import utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * About / Developer Info dialog.
 * Displays application version, developer credits, and tech stack.
 */
public class AboutDialog extends JDialog {

    public AboutDialog(JFrame owner) {
        super(owner, "About " + Constants.APP_NAME, true);
        setSize(480, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        ThemeManager tm = ThemeManager.getInstance();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(tm.bgSecondary());
        setContentPane(root);

        // ── Gradient Header ───────────────────────────────────────────────────
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, tm.gradientStart(),
                    getWidth(), getHeight(), tm.gradientEnd());
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(28, 0, 22, 0));
        header.setOpaque(false);

        JLabel logo = new JLabel("🎯", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(Constants.APP_NAME, SwingConstants.CENTER);
        name.setFont(ThemeManager.FONT_TITLE);
        name.setForeground(tm.textPrimary());
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel version = new JLabel("Version " + Constants.APP_VERSION, SwingConstants.CENTER);
        version.setFont(ThemeManager.FONT_SMALL);
        version.setForeground(tm.textMuted());
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(8));
        header.add(name);
        header.add(Box.createVerticalStrut(4));
        header.add(version);

        root.add(header, BorderLayout.NORTH);

        // ── Info Body ─────────────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setBackground(tm.bgSecondary());
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(16, 36, 16, 36));

        String[][] rows = {
            {"👨‍💻  Developer", Constants.DEVELOPER},
            {"📧  Contact",   Constants.DEVELOPER_EMAIL},
            {"☕  Language",  "Java 17+ with Swing"},
            {"🏛️  Pattern",   "Model-View-Controller (MVC)"},
            {"💾  Persistence", "CSV + Java Serialization"},
            {"🔊  Audio",     "javax.sound.sampled (synthesized)"},
            {"📅  Year",      String.valueOf(Constants.APP_YEAR)},
        };

        for (String[] row : rows) {
            JPanel rowPanel = new JPanel(new BorderLayout(12, 0));
            rowPanel.setOpaque(false);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            JLabel key = new JLabel(row[0]);
            key.setFont(ThemeManager.FONT_BODY);
            key.setForeground(tm.textMuted());
            key.setPreferredSize(new Dimension(170, 24));

            JLabel val = new JLabel(row[1]);
            val.setFont(new Font("Segoe UI", Font.BOLD, 14));
            val.setForeground(tm.textPrimary());

            rowPanel.add(key, BorderLayout.WEST);
            rowPanel.add(val, BorderLayout.CENTER);
            body.add(rowPanel);
            body.add(Box.createVerticalStrut(6));
        }

        root.add(body, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14));
        footer.setBackground(tm.bgCard());

        JLabel copy = new JLabel("© " + Constants.APP_YEAR
            + " " + Constants.DEVELOPER + " · All Rights Reserved");
        copy.setFont(ThemeManager.FONT_SMALL);
        copy.setForeground(tm.textMuted());
        footer.add(copy);

        RoundedButton closeBtn = new RoundedButton("Close", RoundedButton.Variant.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(100, 34));
        closeBtn.addActionListener(e -> dispose());
        footer.add(Box.createHorizontalStrut(16));
        footer.add(closeBtn);

        root.add(footer, BorderLayout.SOUTH);
    }
}
