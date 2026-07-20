package ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Central theme manager for Smart Number Guessing Game Pro.
 *
 * Provides color palettes, fonts, and dimensions for DARK and LIGHT themes.
 * All registered JComponents are automatically repainted when the theme toggles.
 */
public class ThemeManager {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static ThemeManager instance;
    public static ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    // ── Theme Enum ────────────────────────────────────────────────────────────

    public enum Theme { DARK, LIGHT }

    private Theme currentTheme = Theme.DARK;
    private final List<Runnable> changeListeners = new ArrayList<>();

    // ══════════════════════════════════════════════════════════════════════════
    // DARK PALETTE  (Deep Space)
    // ══════════════════════════════════════════════════════════════════════════
    private static final Color D_BG_PRIMARY    = new Color(0x0D1117);
    private static final Color D_BG_SECONDARY  = new Color(0x161B22);
    private static final Color D_BG_CARD       = new Color(0x1C2230);
    private static final Color D_ACCENT        = new Color(0x58A6FF);
    private static final Color D_ACCENT_2      = new Color(0x8B5CF6);
    private static final Color D_SUCCESS       = new Color(0x3FB950);
    private static final Color D_WARNING       = new Color(0xF0883E);
    private static final Color D_DANGER        = new Color(0xF85149);
    private static final Color D_TEXT_PRIMARY  = new Color(0xE6EDF3);
    private static final Color D_TEXT_MUTED    = new Color(0x8B949E);
    private static final Color D_BORDER        = new Color(0x30363D);
    private static final Color D_INPUT_BG      = new Color(0x0D1117);

    // ══════════════════════════════════════════════════════════════════════════
    // LIGHT PALETTE  (Clean Slate)
    // ══════════════════════════════════════════════════════════════════════════
    private static final Color L_BG_PRIMARY    = new Color(0xF6F8FA);
    private static final Color L_BG_SECONDARY  = new Color(0xFFFFFF);
    private static final Color L_BG_CARD       = new Color(0xEBEFF4);
    private static final Color L_ACCENT        = new Color(0x0969DA);
    private static final Color L_ACCENT_2      = new Color(0x7C3AED);
    private static final Color L_SUCCESS       = new Color(0x1A7F37);
    private static final Color L_WARNING       = new Color(0xBF4700);
    private static final Color L_DANGER        = new Color(0xCF222E);
    private static final Color L_TEXT_PRIMARY  = new Color(0x1F2328);
    private static final Color L_TEXT_MUTED    = new Color(0x636C76);
    private static final Color L_BORDER        = new Color(0xD0D7DE);
    private static final Color L_INPUT_BG      = new Color(0xFFFFFF);

    // ── Font Definitions ──────────────────────────────────────────────────────

    public static final Font FONT_HEADER   = new Font("Segoe UI", Font.BOLD,  28);
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MONO_LG  = new Font("Consolas", Font.BOLD,  48);
    public static final Font FONT_MONO_SM  = new Font("Consolas", Font.PLAIN, 16);
    public static final Font FONT_EMOJI    = new Font("Segoe UI Emoji", Font.PLAIN, 20);
    public static final Font FONT_BTN      = new Font("Segoe UI", Font.BOLD,  14);

    // ── Current Theme Accessors ───────────────────────────────────────────────

    public boolean isDark() { return currentTheme == Theme.DARK; }
    public Theme   getTheme() { return currentTheme; }

    public Color bgPrimary()   { return isDark() ? D_BG_PRIMARY   : L_BG_PRIMARY;   }
    public Color bgSecondary() { return isDark() ? D_BG_SECONDARY : L_BG_SECONDARY; }
    public Color bgCard()      { return isDark() ? D_BG_CARD      : L_BG_CARD;      }
    public Color accent()      { return isDark() ? D_ACCENT       : L_ACCENT;       }
    public Color accent2()     { return isDark() ? D_ACCENT_2     : L_ACCENT_2;     }
    public Color success()     { return isDark() ? D_SUCCESS      : L_SUCCESS;      }
    public Color warning()     { return isDark() ? D_WARNING      : L_WARNING;      }
    public Color danger()      { return isDark() ? D_DANGER       : L_DANGER;       }
    public Color textPrimary() { return isDark() ? D_TEXT_PRIMARY : L_TEXT_PRIMARY; }
    public Color textMuted()   { return isDark() ? D_TEXT_MUTED   : L_TEXT_MUTED;   }
    public Color border()      { return isDark() ? D_BORDER       : L_BORDER;       }
    public Color inputBg()     { return isDark() ? D_INPUT_BG     : L_INPUT_BG;     }

    /** Gradient start color for header backgrounds. */
    public Color gradientStart() { return isDark()
        ? new Color(0x1C2A4A) : new Color(0xDFEAFC); }
    /** Gradient end color for header backgrounds. */
    public Color gradientEnd()   { return isDark()
        ? new Color(0x0D1117) : new Color(0xF6F8FA); }

    // ── Toggle ────────────────────────────────────────────────────────────────

    public void toggleTheme() {
        currentTheme = isDark() ? Theme.LIGHT : Theme.DARK;
        utils.Logger.getInstance().info("Theme toggled to " + currentTheme);
        notifyListeners();
    }

    public void setTheme(Theme t) {
        currentTheme = t;
        notifyListeners();
    }

    // ── Listener Registration ─────────────────────────────────────────────────

    public void addChangeListener(Runnable r) { changeListeners.add(r); }

    private void notifyListeners() {
        for (Runnable r : changeListeners) {
            SwingUtilities.invokeLater(r);
        }
    }

    // ── Helper: apply base colors to any JComponent ───────────────────────────

    public void style(JComponent c) {
        c.setBackground(bgCard());
        c.setForeground(textPrimary());
        c.setFont(FONT_BODY);
        c.setOpaque(true);
    }
}
