package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom bar-chart panel that renders a list of scores as vertical bars.
 * Used by the analytics dashboard to visualise recent game scores.
 * Bars animate in on first render using a Swing Timer.
 */
public class GraphPanel extends JPanel {

    private final List<Integer> scores     = new ArrayList<>();
    private final List<String>  labels     = new ArrayList<>();
    private double              animFactor = 0.0;   // 0→1 for entrance animation
    private Timer               animTimer;

    // ── Constructor ───────────────────────────────────────────────────────────

    public GraphPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(600, 200));
        initAnimation();
    }

    // ── API ───────────────────────────────────────────────────────────────────

    /** Replace the current data series and re-render. */
    public void setData(List<Integer> scoreList, List<String> labelList) {
        scores.clear();
        labels.clear();
        scores.addAll(scoreList);
        if (labelList != null) labels.addAll(labelList);
        animFactor = 0.0;
        animTimer.restart();
        repaint();
    }

    // ── Animation ─────────────────────────────────────────────────────────────

    private void initAnimation() {
        animTimer = new Timer(16, e -> {
            animFactor = Math.min(1.0, animFactor + 0.04);
            repaint();
            if (animFactor >= 1.0) animTimer.stop();
        });
    }

    // ── Painting ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (scores.isEmpty()) {
            drawPlaceholder(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        ThemeManager tm = ThemeManager.getInstance();

        int w = getWidth(), h = getHeight();
        int padLeft = 40, padBottom = 30, padTop = 10;
        int chartW = w - padLeft - 10;
        int chartH = h - padBottom - padTop;

        int max = scores.stream().mapToInt(Integer::intValue).max().orElse(1);
        if (max == 0) max = 1;

        int n       = scores.size();
        int barGap  = 6;
        int barW    = Math.max(12, (chartW - barGap * (n - 1)) / n);
        int totalW  = barW * n + barGap * (n - 1);
        int startX  = padLeft + (chartW - totalW) / 2;

        // Y-axis grid lines
        g2.setFont(ThemeManager.FONT_SMALL);
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 1f, new float[]{4f, 4f}, 0f));
        int gridLines = 4;
        for (int i = 0; i <= gridLines; i++) {
            int y = padTop + chartH - (chartH * i / gridLines);
            g2.setColor(tm.border());
            g2.drawLine(padLeft, y, w - 10, y);
            g2.setColor(tm.textMuted());
            g2.drawString(String.valueOf(max * i / gridLines), 2, y + 4);
        }
        g2.setStroke(new BasicStroke(1f));

        // Bars
        for (int i = 0; i < n; i++) {
            int score    = scores.get(i);
            double ratio = (double) score / max * animFactor;
            int barH     = (int)(chartH * ratio);
            int x        = startX + i * (barW + barGap);
            int y        = padTop + chartH - barH;

            // Bar gradient
            Color barColor = barColorForIndex(i, tm);
            GradientPaint gp = new GradientPaint(
                x, y, barColor.brighter(),
                x, padTop + chartH, barColor);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(x, y, barW, barH, 6, 6));

            // Score value on top
            if (barH > 20) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(Color.WHITE);
                String txt = String.valueOf(score);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(txt, x + (barW - fm.stringWidth(txt)) / 2, y + 14);
            }

            // X label
            g2.setFont(ThemeManager.FONT_SMALL);
            g2.setColor(tm.textMuted());
            String lbl = (i < labels.size()) ? labels.get(i) : String.valueOf(i + 1);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl,
                x + (barW - fm.stringWidth(lbl)) / 2,
                padTop + chartH + 16);
        }

        g2.dispose();
    }

    private Color barColorForIndex(int i, ThemeManager tm) {
        Color[] palette = {
            tm.accent(), tm.accent2(), tm.success(),
            tm.warning(), new Color(0xEC4899)
        };
        return palette[i % palette.length];
    }

    private void drawPlaceholder(Graphics g) {
        ThemeManager tm = ThemeManager.getInstance();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(tm.textMuted());
        g2.setFont(ThemeManager.FONT_BODY);
        String msg = "No score data yet — play a game!";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg,
            (getWidth() - fm.stringWidth(msg)) / 2,
            getHeight() / 2);
        g2.dispose();
    }
}
