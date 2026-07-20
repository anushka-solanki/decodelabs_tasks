package com.gradecalculator.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ThemeManager {

    private boolean isDarkMode = false;

    // Light Theme Colors
    private final Color bgLight = new Color(245, 245, 250);
    private final Color cardLight = new Color(255, 255, 255);
    private final Color textLight = new Color(33, 33, 33);
    private final Color secondaryTextLight = new Color(100, 100, 100);
    private final Color primaryLight = new Color(63, 81, 181);

    // Dark Theme Colors
    private final Color bgDark = new Color(30, 30, 30);
    private final Color cardDark = new Color(45, 45, 45);
    private final Color textDark = new Color(230, 230, 230);
    private final Color secondaryTextDark = new Color(170, 170, 170);
    private final Color primaryDark = new Color(121, 134, 203);

    // Fonts
    public final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    public final Font headerFont = new Font("Segoe UI", Font.BOLD, 18);
    public final Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    public final Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

    public void toggleTheme() {
        isDarkMode = !isDarkMode;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public Color getBackgroundColor() {
        return isDarkMode ? bgDark : bgLight;
    }

    public Color getCardColor() {
        return isDarkMode ? cardDark : cardLight;
    }

    public Color getTextColor() {
        return isDarkMode ? textDark : textLight;
    }
    
    public Color getSecondaryTextColor() {
        return isDarkMode ? secondaryTextDark : secondaryTextLight;
    }

    public Color getPrimaryColor() {
        return isDarkMode ? primaryDark : primaryLight;
    }
    
    public Color getPassColor() {
        return new Color(76, 175, 80);
    }
    
    public Color getFailColor() {
        return new Color(244, 67, 54);
    }

    public Border getCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isDarkMode ? new Color(70, 70, 70) : new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    public void applyTheme(Container container) {
        container.setBackground(getBackgroundColor());
        applyThemeRecursively(container);
    }

    private void applyThemeRecursively(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                if (c.getName() != null && c.getName().equals("Card")) {
                    c.setBackground(getCardColor());
                } else {
                    c.setBackground(getBackgroundColor());
                }
            } else if (c instanceof JLabel) {
                c.setForeground(getTextColor());
            } else if (c instanceof JTextField) {
                c.setBackground(getCardColor());
                c.setForeground(getTextColor());
                ((JTextField) c).setCaretColor(getTextColor());
            } else if (c instanceof JButton) {
                // simple button styling
                c.setBackground(getPrimaryColor());
                c.setForeground(Color.WHITE);
                c.setFont(boldFont);
            } else if (c instanceof JTable) {
                c.setBackground(getCardColor());
                c.setForeground(getTextColor());
                ((JTable) c).getTableHeader().setBackground(getPrimaryColor());
                ((JTable) c).getTableHeader().setForeground(Color.WHITE);
            } else if (c instanceof JScrollPane) {
                c.setBackground(getBackgroundColor());
                ((JScrollPane) c).getViewport().setBackground(getBackgroundColor());
            }
            
            if (c instanceof Container) {
                applyThemeRecursively((Container) c);
            }
        }
    }
}
