package com.gradecalculator;

import com.gradecalculator.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel as a base
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
