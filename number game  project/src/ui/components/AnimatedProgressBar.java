package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Smooth animated progress bar that transitions to its target value
 * using a Swing Timer for fluid animation.
 * Color shifts from green → orange → red as progress increases.
 */
public class AnimatedProgressBar extends JComponent {

    private double displayValue  = 0.0;  // current rendered value (0.0–1.0)
    private double targetValue   = 0.0;  // target to animate toward
    private final int arcRadius  = 10;
    private Timer animTimer;

    // ── Constructor ───────────────────────────────────────────────────────────

    public AnimatedProgressBar() {
        setPreferredSize(new Dimension(400, 22));
        setOpaque(false);
        initAnimation();
    }

    private void initAnimation() {
        animTimer = new Timer(16, e -> {          // ~60 fps
            double delta = targetValue - displayValue;
            if (Math.abs(delta) < 0.005) {
                displayValue = targetValue;
                animTimer.stop();
            } else {
                displayValue += delta * 0.12;    // ease-out factor
            }
            repaint();
        });
    }

    // ── API ───────────────────────────────────────────────────────────────────

    /**
     * Smoothly animates the bar to the given fraction (0.0–1.0).
     */
    public void setProgress(double fraction) {
        targetValue = Math.max(0.0, Math.min(1.0, fraction));
        if (!animTimer.isRunning()) animTimer.restart();
    }

    /** Instantly resets the bar to 0 without animation. */
    public void reset() {
        displayValue = 0.0;
        targetValue  = 0.0;
        animTimer.stop();
        repaint();
    }

    // ── Painting ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ThemeManager tm = ThemeManager.getInstance();
        int w = getWidth(), h = getHeight();
        int arc = arcRadius * 2;

        // Track (background)
        g2.setColor(tm.border());
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        // Fill
        int fillW = (int)(w * displayValue);
        if (fillW > 0) {
            Color fillColor = interpolateColor(displayValue);
            GradientPaint gp = new GradientPaint(
                0, 0, fillColor.brighter(),
                fillW, h, fillColor);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, fillW, h, arc, arc));

            // Shine overlay
            g2.setColor(new Color(255, 255, 255, 28));
            g2.fill(new RoundRectangle2D.Float(0, 0, fillW, h / 2f, arc, arc));
        }

        // Percentage label
        String label = (int)(displayValue * 100) + "%";
        g2.setFont(ThemeManager.FONT_SMALL);
        g2.setColor(tm.textPrimary());
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(label)) / 2;
        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(label, tx, ty);

        g2.dispose();
    }

    // ── Color interpolation (green → orange → red) ────────────────────────────

    private Color interpolateColor(double t) {
        // t: 0.0 = green, 0.5 = orange, 1.0 = red
        if (t < 0.5) {
            return blend(new Color(0x3FB950), new Color(0xF0883E), t * 2);
        } else {
            return blend(new Color(0xF0883E), new Color(0xF85149), (t - 0.5) * 2);
        }
    }

    private Color blend(Color a, Color b, double t) {
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl= (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, bl)));
    }
}
