package ui;

import ui.components.ThemeManager;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Transparent confetti overlay panel that plays a celebratory animation
 * when the player wins a game.
 *
 * Added to a JLayeredPane above all other content; automatically removes
 * itself after the animation completes.
 */
public class ConfettiPanel extends JPanel {

    // ── Confetti particle ─────────────────────────────────────────────────────

    private static class Particle {
        double x, y, vx, vy, rotation, rotSpeed, size;
        Color color;
        Shape shape; // circle or rect

        Particle(int panelW, int panelH, Random rng) {
            x         = rng.nextInt(panelW);
            y         = -rng.nextInt(panelH / 2) - 10;  // start above panel
            vx        = (rng.nextDouble() - 0.5) * 3.0;
            vy        = rng.nextDouble() * 3.0 + 1.5;
            rotation  = rng.nextDouble() * 360;
            rotSpeed  = (rng.nextDouble() - 0.5) * 8.0;
            size      = rng.nextDouble() * 12 + 5;
            shape     = rng.nextBoolean() ? new Ellipse2D.Double(0, 0, size, size * 0.6)
                                          : new Rectangle.Double(0, 0, size, size * 0.4);

            // Vibrant palette
            Color[] palette = {
                new Color(0xFF6B6B), new Color(0xFFE66D), new Color(0x4ECDC4),
                new Color(0x95E1D3), new Color(0xA8E6CF), new Color(0xFFD93D),
                new Color(0x6C5CE7), new Color(0xFD79A8), new Color(0x00CEC9)
            };
            color = palette[rng.nextInt(palette.length)];
        }

        void update() {
            x        += vx;
            y        += vy;
            vy       += 0.04;          // gravity
            rotation += rotSpeed;
        }

        boolean isOffScreen(int h) { return y > h + 20; }
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    private final List<Particle> particles = new ArrayList<>();
    private final Timer          ticker;
    private int                  elapsed   = 0;
    private final int            durationMs;
    private final Container      parent;

    // ── Constructor ───────────────────────────────────────────────────────────

    public ConfettiPanel(Container parent) {
        this.parent     = parent;
        this.durationMs = Constants.CONFETTI_DURATION;
        setOpaque(false);
        setIgnoreRepaint(false);

        Random rng = new Random();
        int w = parent.getWidth()  > 0 ? parent.getWidth()  : 800;
        int h = parent.getHeight() > 0 ? parent.getHeight() : 600;

        for (int i = 0; i < Constants.CONFETTI_COUNT; i++) {
            particles.add(new Particle(w, h, rng));
        }

        ticker = new Timer(Constants.CONFETTI_TICK_MS, e -> {
            elapsed += Constants.CONFETTI_TICK_MS;
            particles.forEach(Particle::update);
            particles.removeIf(p -> p.isOffScreen(h));
            repaint();
            if (elapsed >= durationMs || particles.isEmpty()) {
                stop();
            }
        });
    }

    // ── Control ───────────────────────────────────────────────────────────────

    public void start() { ticker.start(); }

    public void stop() {
        ticker.stop();
        // Remove self from parent layer
        if (parent instanceof JLayeredPane lp) {
            lp.remove(this);
            lp.repaint();
        } else {
            SwingUtilities.invokeLater(() -> {
                Container c = getParent();
                if (c != null) { c.remove(this); c.repaint(); }
            });
        }
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fade out in the last 500ms
        double alpha = elapsed < durationMs - 500 ? 1.0
                     : 1.0 - (double)(elapsed - (durationMs - 500)) / 500.0;

        for (Particle p : particles) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                (float) Math.max(0, Math.min(1, alpha))));
            g2.setColor(p.color);
            g2.translate(p.x, p.y);
            g2.rotate(Math.toRadians(p.rotation), p.size / 2, p.size / 4);
            g2.fill(p.shape);
            g2.rotate(-Math.toRadians(p.rotation), p.size / 2, p.size / 4);
            g2.translate(-p.x, -p.y);
        }

        g2.dispose();
    }
}
