package com.gradecalculator.util;

import com.gradecalculator.model.Result;
import com.gradecalculator.model.Subject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

    public static boolean exportToCSV(Result result, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Student Name," + result.getStudent().getName() + "\n");
            writer.write("Roll Number," + result.getStudent().getRollNumber() + "\n");
            writer.write("Course," + result.getStudent().getCourse() + "\n");
            writer.write("Semester," + result.getStudent().getSemester() + "\n");
            writer.write("\n");
            
            writer.write("Subject Name,Marks Obtained,Max Marks,Percentage\n");
            for (Subject sub : result.getStudent().getSubjects()) {
                writer.write(String.format("%s,%.2f,%.2f,%.2f%%\n", 
                    sub.getName(), sub.getMarksObtained(), sub.getMaxMarks(), sub.getPercentage()));
            }
            
            writer.write("\n");
            writer.write(String.format("Total Marks,%.2f / %.2f\n", result.getTotalMarksObtained(), result.getMaxTotalMarks()));
            writer.write(String.format("Average Percentage,%.2f%%\n", result.getAveragePercentage()));
            writer.write("Overall Grade," + result.getOverallGrade() + "\n");
            writer.write("Status," + result.getStatus() + "\n");
            writer.write("Performance," + result.getPerformanceLevel() + "\n");
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void appendHistory(Result result) {
        File file = new File("history.csv");
        boolean fileExists = file.exists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                writer.write("Name,Roll Number,Total Marks,Percentage,Grade,Status\n");
            }
            writer.write(String.format("%s,%s,%.2f / %.2f,%.2f%%,%s,%s\n",
                    result.getStudent().getName(),
                    result.getStudent().getRollNumber(),
                    result.getTotalMarksObtained(),
                    result.getMaxTotalMarks(),
                    result.getAveragePercentage(),
                    result.getOverallGrade(),
                    result.getStatus()));
        } catch (IOException e) {
            System.err.println("Failed to save history: " + e.getMessage());
        }
    }
}
