package com.gradecalculator.model;

import java.util.List;

public class Result {
    private Student student;
    private double totalMarksObtained;
    private double maxTotalMarks;
    private double averagePercentage;
    private String overallGrade;
    private String status;
    private String performanceLevel;
    
    private Subject bestSubject;
    private Subject lowestSubject;
    private String personalizedMessage;

    public Result(Student student) {
        this.student = student;
        calculate();
    }

    private void calculate() {
        List<Subject> subjects = student.getSubjects();
        if (subjects == null || subjects.isEmpty()) {
            return;
        }

        totalMarksObtained = 0;
        maxTotalMarks = 0;
        boolean hasFailedSubject = false;
        
        Subject best = subjects.get(0);
        Subject lowest = subjects.get(0);

        for (Subject sub : subjects) {
            totalMarksObtained += sub.getMarksObtained();
            maxTotalMarks += sub.getMaxMarks();
            
            if (sub.getPercentage() < GradingRules.PASS_MIN) {
                hasFailedSubject = true;
            }
            
            if (sub.getPercentage() > best.getPercentage()) {
                best = sub;
            }
            if (sub.getPercentage() < lowest.getPercentage()) {
                lowest = sub;
            }
        }

        averagePercentage = (maxTotalMarks == 0) ? 0 : (totalMarksObtained / maxTotalMarks) * 100.0;
        overallGrade = GradingRules.getGrade(averagePercentage);
        status = hasFailedSubject ? "FAIL" : GradingRules.getStatus(averagePercentage);
        performanceLevel = GradingRules.getPerformanceLevel(averagePercentage);
        
        this.bestSubject = best;
        this.lowestSubject = lowest;
        
        generatePersonalizedMessage();
    }
    
    private void generatePersonalizedMessage() {
        if (averagePercentage >= 90) {
            personalizedMessage = "Excellent performance! Keep maintaining your consistency.";
        } else if (averagePercentage >= 70) {
            personalizedMessage = "Your performance is good, but you can improve your marks in " + lowestSubject.getName() + ".";
        } else if (averagePercentage >= 40) {
            personalizedMessage = "Focus on subjects below 50% to improve your overall percentage.";
        } else {
            personalizedMessage = "You need serious preparation in all subjects, especially " + lowestSubject.getName() + ".";
        }
    }

    public Student getStudent() { return student; }
    public double getTotalMarksObtained() { return totalMarksObtained; }
    public double getMaxTotalMarks() { return maxTotalMarks; }
    public double getAveragePercentage() { return averagePercentage; }
    public String getOverallGrade() { return overallGrade; }
    public String getStatus() { return status; }
    public String getPerformanceLevel() { return performanceLevel; }
    public Subject getBestSubject() { return bestSubject; }
    public Subject getLowestSubject() { return lowestSubject; }
    public String getPersonalizedMessage() { return personalizedMessage; }
}
