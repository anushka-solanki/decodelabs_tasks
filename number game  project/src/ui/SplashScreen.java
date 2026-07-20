package ui;

import ui.components.ThemeManager;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Animated splash screen shown on application startup.
 *
 * Features:
 *  - Gradient background with animated progress bar
 *  - Pulsing logo emoji
 *  - Fade-out transition before handing control to MainWindow
 */
public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private float              alpha = 1.0f;
    private Timer              fadeTimer;
    private Runnable           onComplete;
    private int                pulsePhase = 0;
    private Timer              pulseTimer;

    // ── Constructor ───────────────────────────────────────────────────────────

    public SplashScreen() {
        setSize(Constants.SPLASH_WIDTH, Constants.SPLASH_HEIGHT);
        setLocationRelativeTo(null);

        try {
            setOpacity(1.0f);
        } catch (Exception ignored) {}

        // Background panel with custom painting
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0x0D1117),
                    getWidth(), getHeight(), new Color(0x1C2A4A));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Accent border
                g2.setColor(new Color(0x58A6FF, true));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 18, 18);

                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setContentPane(panel);

        buildContent(panel);
    }

    private void buildContent(JPanel panel) {
        // Logo
        JLabel logo = new JLabel("🎯", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // Title
        JLabel title = new JLabel(Constants.APP_NAME, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0xE6EDF3));

        // Subtitle
        JLabel sub = new JLabel("✦  Guess · Win · Dominate  ✦", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        sub.setForeground(new Color(0x58A6FF));
        sub.setBorder(BorderFactory.createEmptyBorder(4, 0, 24, 0));

        // Progress bar (styled)
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(360, 6));
        progressBar.setBackground(new Color(0x30363D));
        progressBar.setForeground(new Color(0x58A6FF));
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(true);

        // Loading label
        JLabel loading = new JLabel("Loading …", SwingConstants.CENTER);
        loading.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loading.setForeground(new Color(0x8B949E));
        loading.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Version
        JLabel ver = new JLabel("v" + Constants.APP_VERSION, SwingConstants.CENTER);
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(new Color(0x58A6FF));

        // Layout
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[]{logo, title, sub, progressBar, loading}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(c);
        }

        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(ver);
        panel.add(bottom, BorderLayout.SOUTH);

        // Pulse animation on logo
        pulseTimer = new Timer(80, null);
        int[] pulsePhases = {0};
        pulseTimer.addActionListener(e -> {
            pulsePhases[0]++;
            float scale = 1.0f + 0.05f * (float)Math.sin(pulsePhases[0] * 0.3);
            logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int)(72 * scale)));
        });
        pulseTimer.start();
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Shows the splash, animates the progress bar over {@code durationMs},
     * then fades out and calls {@code onComplete}.
     */
    public void showAndAnimate(Runnable onComplete) {
        this.onComplete = onComplete;
        setVisible(true);

        int steps        = 100;
        int stepDelay    = Constants.SPLASH_DURATION / steps;
        Timer progressTicker = new Timer(stepDelay, null);
        final int[] step = {0};

        progressTicker.addActionListener(e -> {
            step[0]++;
            progressBar.setValue(step[0]);
            if (step[0] >= steps) {
                progressTicker.stop();
                pulseTimer.stop();
                beginFadeOut();
            }
        });
        progressTicker.start();
    }

    private void beginFadeOut() {
        try { setOpacity(1.0f); } catch (Exception ignored) {}
        fadeTimer = new Timer(30, null);
        fadeTimer.addActionListener(e -> {
            alpha -= 0.06f;
            if (alpha <= 0f) {
                alpha = 0f;
                fadeTimer.stop();
                setVisible(false);
                dispose();
                if (onComplete != null) SwingUtilities.invokeLater(onComplete);
            }
            try { setOpacity(Math.max(0f, alpha)); } catch (Exception ignored) {}
        });
        fadeTimer.start();
    }
}
