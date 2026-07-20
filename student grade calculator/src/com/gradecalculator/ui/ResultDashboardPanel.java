package com.gradecalculator.ui;

import com.gradecalculator.model.Result;
import com.gradecalculator.util.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class ResultDashboardPanel extends JPanel {

    private ThemeManager themeManager;
    private Result result;

    public ResultDashboardPanel(ThemeManager themeManager, Result result, ActionListener onBack) {
        this.themeManager = themeManager;
        this.result = result;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setName("Background");

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createActionPanel(onBack), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
        panel.setName("Card");
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        
        panel.add(new JLabel("Name: " + result.getStudent().getName()));
        panel.add(new JLabel("Roll No: " + result.getStudent().getRollNumber()));
        panel.add(new JLabel("Course: " + result.getStudent().getCourse()));
        panel.add(new JLabel("Semester: " + result.getStudent().getSemester()));
        
        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setName("Background");
        
        // Highlights (Percentage, Grade, Status)
        JPanel highlightsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        highlightsPanel.setName("Background");
        
        highlightsPanel.add(createHighlightCard("Percentage", String.format("%.2f%%", result.getAveragePercentage())));
        highlightsPanel.add(createHighlightCard("Grade", result.getOverallGrade()));
        
        JPanel statusCard = createHighlightCard("Status", result.getStatus());
        JLabel statusLabel = (JLabel) statusCard.getComponent(1);
        statusLabel.setForeground(result.getStatus().equals("PASS") ? themeManager.getPassColor() : themeManager.getFailColor());
        highlightsPanel.add(statusCard);
        
        // Insights
        JPanel insightsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        insightsPanel.setName("Card");
        insightsPanel.setBorder(BorderFactory.createTitledBorder("Performance Insights"));
        
        insightsPanel.add(new JLabel("Total Marks: " + result.getTotalMarksObtained() + " / " + result.getMaxTotalMarks()));
        insightsPanel.add(new JLabel("Best Subject: " + result.getBestSubject().getName() + " (" + result.getBestSubject().getMarksObtained() + ")"));
        insightsPanel.add(new JLabel("Lowest Subject: " + result.getLowestSubject().getName() + " (" + result.getLowestSubject().getMarksObtained() + ")"));
        
        JLabel msgLabel = new JLabel("<html><i>" + result.getPersonalizedMessage() + "</i></html>");
        msgLabel.setForeground(themeManager.getPrimaryColor());
        insightsPanel.add(msgLabel);

        panel.add(highlightsPanel, BorderLayout.NORTH);
        panel.add(insightsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHighlightCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setName("Card");
        card.setBorder(themeManager.getCardBorder());
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(themeManager.regularFont);
        titleLabel.setForeground(themeManager.getSecondaryTextColor());
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createActionPanel(ActionListener onBack) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setName("Background");
        
        JButton backBtn = new JButton("Back to Entry");
        backBtn.addActionListener(onBack);
        
        JButton saveBtn = new JButton("Export to CSV");
        saveBtn.addActionListener(e -> exportResult());
        
        panel.add(backBtn);
        panel.add(saveBtn);
        
        return panel;
    }
    
    private void exportResult() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Result as CSV");
        fileChooser.setSelectedFile(new File(result.getStudent().getName() + "_result.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (FileHandler.exportToCSV(result, fileToSave)) {
                JOptionPane.showMessageDialog(this, "Result exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export result.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
