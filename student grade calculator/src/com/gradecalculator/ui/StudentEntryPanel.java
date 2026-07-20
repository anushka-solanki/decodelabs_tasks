package com.gradecalculator.ui;

import com.gradecalculator.model.Student;
import com.gradecalculator.model.Subject;
import com.gradecalculator.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class StudentEntryPanel extends JPanel {

    private ThemeManager themeManager;
    
    private JTextField nameField;
    private JTextField rollNoField;
    private JTextField courseField;
    private JTextField semesterField;
    
    private JTextField subjectNameField;
    private JTextField marksField;
    private JTextField maxMarksField;
    
    private DefaultTableModel tableModel;
    private JTable subjectTable;
    
    private JButton calculateBtn;
    private JButton addSubjectBtn;
    private JButton deleteSubjectBtn;

    public StudentEntryPanel(ThemeManager themeManager, ActionListener onCalculate) {
        this.themeManager = themeManager;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = createStudentDetailsCard();
        JPanel centerPanel = createSubjectsCard();
        JPanel bottomPanel = createActionPanel(onCalculate);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setName("Background");
    }

    private JPanel createStudentDetailsCard() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setName("Card");
        panel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        
        panel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        
        panel.add(new JLabel("Roll Number:"));
        rollNoField = new JTextField();
        panel.add(rollNoField);
        
        panel.add(new JLabel("Course/Class:"));
        courseField = new JTextField();
        panel.add(courseField);
        
        panel.add(new JLabel("Semester (Optional):"));
        semesterField = new JTextField();
        panel.add(semesterField);

        return panel;
    }

    private JPanel createSubjectsCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setName("Card");
        panel.setBorder(BorderFactory.createTitledBorder("Subjects & Marks"));

        // Entry area
        JPanel entryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        entryPanel.setName("Card");
        entryPanel.add(new JLabel("Subject:"));
        subjectNameField = new JTextField(15);
        entryPanel.add(subjectNameField);
        entryPanel.add(new JLabel("Obtained Marks:"));
        marksField = new JTextField(5);
        entryPanel.add(marksField);
        entryPanel.add(new JLabel("Max Marks:"));
        maxMarksField = new JTextField(5);
        entryPanel.add(maxMarksField);
        
        addSubjectBtn = new JButton("Add Subject");
        addSubjectBtn.addActionListener(e -> addSubject());
        entryPanel.add(addSubjectBtn);

        // Table
        String[] columns = {"Subject Name", "Marks Obtained", "Max Marks", "Percentage"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        subjectTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(subjectTable);
        
        // Actions
        JPanel tableActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableActionPanel.setName("Card");
        deleteSubjectBtn = new JButton("Delete Selected");
        deleteSubjectBtn.addActionListener(e -> deleteSubject());
        JButton clearAllBtn = new JButton("Clear All");
        clearAllBtn.addActionListener(e -> tableModel.setRowCount(0));
        tableActionPanel.add(deleteSubjectBtn);
        tableActionPanel.add(clearAllBtn);

        panel.add(entryPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableActionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createActionPanel(ActionListener onCalculate) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setName("Background");
        
        calculateBtn = new JButton("Calculate Result");
        calculateBtn.setPreferredSize(new Dimension(200, 40));
        calculateBtn.addActionListener(onCalculate);
        
        JButton resetBtn = new JButton("Reset Form");
        resetBtn.setPreferredSize(new Dimension(150, 40));
        resetBtn.addActionListener(e -> resetForm());

        panel.add(calculateBtn);
        panel.add(resetBtn);

        return panel;
    }
    
    private void addSubject() {
        String name = subjectNameField.getText().trim();
        String marksStr = marksField.getText().trim();
        String maxMarksStr = maxMarksField.getText().trim();
        
        if (ValidationUtil.isNullOrEmpty(name)) {
            JOptionPane.showMessageDialog(this, "Subject name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ValidationUtil.isNullOrEmpty(maxMarksStr)) {
            JOptionPane.showMessageDialog(this, "Maximum marks cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double maxMarks;
            try {
                maxMarks = Double.parseDouble(maxMarksStr);
                if (maxMarks <= 0) {
                    JOptionPane.showMessageDialog(this, "Maximum marks must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Maximum marks must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double marks = ValidationUtil.parseMarks(marksStr, maxMarks);
            
            // Check duplicates in table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(name)) {
                    JOptionPane.showMessageDialog(this, "Subject already added.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            double percentage = (marks / maxMarks) * 100.0;
            tableModel.addRow(new Object[]{name, marks, maxMarks, String.format("%.2f%%", percentage)});
            subjectNameField.setText("");
            marksField.setText("");
            maxMarksField.setText("");
            subjectNameField.requestFocus();
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void resetForm() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all entered data?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            nameField.setText("");
            rollNoField.setText("");
            courseField.setText("");
            semesterField.setText("");
            subjectNameField.setText("");
            marksField.setText("");
            maxMarksField.setText("");
            tableModel.setRowCount(0);
        }
    }

    public Student getStudentData() throws IllegalArgumentException {
        String name = nameField.getText().trim();
        String roll = rollNoField.getText().trim();
        String course = courseField.getText().trim();
        String semester = semesterField.getText().trim();
        
        if (ValidationUtil.isNullOrEmpty(name) || ValidationUtil.isNullOrEmpty(roll)) {
            throw new IllegalArgumentException("Student Name and Roll Number are required.");
        }
        
        if (tableModel.getRowCount() == 0) {
            throw new IllegalArgumentException("Please add at least one subject to calculate results.");
        }
        
        Student student = new Student(name, roll, course, semester);
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String subName = tableModel.getValueAt(i, 0).toString();
            double marks = Double.parseDouble(tableModel.getValueAt(i, 1).toString());
            double max = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
            student.addSubject(new Subject(subName, marks, max));
        }
        
        return student;
    }
}
