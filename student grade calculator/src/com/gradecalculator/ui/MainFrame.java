package com.gradecalculator.ui;

import com.gradecalculator.model.Result;
import com.gradecalculator.model.Student;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private ThemeManager themeManager;
    private JPanel mainContainer;
    private CardLayout cardLayout;
    
    private StudentEntryPanel entryPanel;

    public MainFrame() {
        themeManager = new ThemeManager();
        
        setTitle("Student Grade Calculator");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(themeManager.getPrimaryColor());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Student Grade Calculator");
        titleLabel.setFont(themeManager.titleFont);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton openFolderBtn = new JButton("Open Folder");
        openFolderBtn.setFocusPainted(false);
        openFolderBtn.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new java.io.File(System.getProperty("user.dir")));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to open folder.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton themeToggleBtn = new JButton("Toggle Theme");
        themeToggleBtn.setFocusPainted(false);
        themeToggleBtn.addActionListener(e -> {
            themeManager.toggleTheme();
            themeManager.applyTheme(this.getContentPane());
            SwingUtilities.updateComponentTreeUI(this);
        });
        
        buttonsPanel.add(openFolderBtn);
        buttonsPanel.add(themeToggleBtn);
        
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Area with CardLayout
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        entryPanel = new StudentEntryPanel(themeManager, e -> calculateAndShowResult());
        mainContainer.add(entryPanel, "ENTRY");
        
        add(mainContainer, BorderLayout.CENTER);
        
        // Initial Theme Apply
        themeManager.applyTheme(this.getContentPane());
    }

    private void calculateAndShowResult() {
        try {
            Student student = entryPanel.getStudentData();
            Result result = new Result(student);
            
            // Automatically log to history
            com.gradecalculator.util.FileHandler.appendHistory(result);
            
            ResultDashboardPanel resultPanel = new ResultDashboardPanel(themeManager, result, e -> showEntryPanel());
            
            // Remove old result panel if exists
            for (Component comp : mainContainer.getComponents()) {
                if (comp instanceof ResultDashboardPanel) {
                    mainContainer.remove(comp);
                }
            }
            
            mainContainer.add(resultPanel, "RESULT");
            themeManager.applyTheme(resultPanel);
            
            cardLayout.show(mainContainer, "RESULT");
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEntryPanel() {
        cardLayout.show(mainContainer, "ENTRY");
    }
}
