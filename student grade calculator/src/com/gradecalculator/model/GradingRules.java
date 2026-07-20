package com.gradecalculator.model;

public class GradingRules {
    
    // Configurable boundaries
    public static final double A_PLUS_MIN = 90.0;
    public static final double A_MIN = 80.0;
    public static final double B_PLUS_MIN = 70.0;
    public static final double B_MIN = 60.0;
    public static final double C_MIN = 50.0;
    public static final double D_MIN = 40.0;
    
    public static final double PASS_MIN = 40.0;

    public static String getGrade(double percentage) {
        if (percentage >= A_PLUS_MIN) return "A+";
        if (percentage >= A_MIN) return "A";
        if (percentage >= B_PLUS_MIN) return "B+";
        if (percentage >= B_MIN) return "B";
        if (percentage >= C_MIN) return "C";
        if (percentage >= D_MIN) return "D";
        return "F";
    }
    
    public static String getPerformanceLevel(double percentage) {
        if (percentage >= A_PLUS_MIN) return "Excellent";
        if (percentage >= A_MIN) return "Very Good";
        if (percentage >= B_PLUS_MIN) return "Good";
        if (percentage >= B_MIN) return "Good";
        if (percentage >= C_MIN) return "Needs Improvement";
        if (percentage >= D_MIN) return "Needs Improvement";
        return "Failed";
    }
    
    public static String getStatus(double percentage) {
        return percentage >= PASS_MIN ? "PASS" : "FAIL";
    }
}
