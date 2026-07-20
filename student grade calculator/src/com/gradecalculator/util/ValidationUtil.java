package com.gradecalculator.util;

import java.util.List;

public class ValidationUtil {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static Double parseMarks(String marksStr, double maxMarks) throws IllegalArgumentException {
        if (isNullOrEmpty(marksStr)) {
            throw new IllegalArgumentException("Marks cannot be empty.");
        }
        try {
            double marks = Double.parseDouble(marksStr);
            if (marks < 0) {
                throw new IllegalArgumentException("Marks cannot be negative.");
            }
            if (marks > maxMarks) {
                throw new IllegalArgumentException("Obtained marks cannot be greater than maximum marks.");
            }
            return marks;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Marks must be a valid number.");
        }
    }

    public static boolean isDuplicateSubject(String subjectName, List<com.gradecalculator.model.Subject> existingSubjects) {
        if (isNullOrEmpty(subjectName)) return false;
        String lowerName = subjectName.trim().toLowerCase();
        for (com.gradecalculator.model.Subject sub : existingSubjects) {
            if (sub.getName().toLowerCase().equals(lowerName)) {
                return true;
            }
        }
        return false;
    }
}
