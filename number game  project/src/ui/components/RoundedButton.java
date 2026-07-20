package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom rounded-corner JButton with hover/press effects and gradient fill.
 * Uses {@link ThemeManager} for color tokens.
 */
public class RoundedButton extends JButton {

    // ── Variant enum ──────────────────────────────────────────────────────────

    public enum Variant { PRIMARY, SECONDARY, SUCCESS, DANGER, WARNING, GHOST }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final Variant variant;
    private final int     arcRadius;
    private boolean       hovered  = false;
    private boolean        pressed  = false;

    // ── Constructor ───────────────────────────────────────────────────────────

    public RoundedButton(String text, Variant variant) {
        super(text);
        this.variant   = variant;
        this.arcRadius = 14;
        configure();
    }

    public RoundedButton(String text) {
        this(text, Variant.PRIMARY);
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void configure() {
        setFont(ThemeManager.FONT_BTN);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(getPreferredSize().width + 24, 40));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
            @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
        });
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        ThemeManager tm = ThemeManager.getInstance();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, arcRadius * 2, arcRadius * 2);

        // Background
        Color base = getBaseColor(tm);
        Color fill = pressed ? darken(base, 0.18f)
                   : hovered ? lighten(base, 0.12f)
                   : base;

        // Gradient fill
        GradientPaint gp = new GradientPaint(
            0, 0, fill,
            0, h, darken(fill, 0.10f)
        );
        g2.setPaint(gp);
        g2.fill(shape);

        // Subtle border
        g2.setColor(darken(base, 0.25f));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(shape);

        // Shadow / glow when hovered
        if (hovered && !pressed) {
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 50));
            g2.setStroke(new BasicStroke(3f));
            g2.draw(new RoundRectangle2D.Float(-1, -1, w + 2, h + 2,
                    arcRadius * 2 + 2, arcRadius * 2 + 2));
        }

        g2.dispose();

        // Text via super (with transparency preserved)
        setForeground(getTextColor(tm));
        super.paintComponent(g);
    }

    // ── Color Helpers ─────────────────────────────────────────────────────────

    private Color getBaseColor(ThemeManager tm) {
        return switch (variant) {
            case PRIMARY   -> tm.accent();
            case SECONDARY -> tm.bgCard();
            case SUCCESS   -> tm.success();
            case DANGER    -> tm.danger();
            case WARNING   -> tm.warning();
            case GHOST     -> new Color(tm.textMuted().getRed(),
                                        tm.textMuted().getGreen(),
                                        tm.textMuted().getBlue(), 30);
        };
    }

    private Color getTextColor(ThemeManager tm) {
        return switch (variant) {
            case SECONDARY, GHOST -> tm.textPrimary();
            default               -> Color.WHITE;
        };
    }

    private Color darken(Color c, float amount) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsb[2] = Math.max(0f, hsb[2] - amount);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    private Color lighten(Color c, float amount) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsb[2] = Math.min(1f, hsb[2] + amount);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
}
