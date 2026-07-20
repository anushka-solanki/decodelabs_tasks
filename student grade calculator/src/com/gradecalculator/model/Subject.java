package com.gradecalculator.model;

public class Subject {
    private String name;
    private double marksObtained;
    private double maxMarks;

    public Subject(String name, double marksObtained, double maxMarks) {
        this.name = name;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
    }

    public String getName() {
        return name;
    }

    public double getMarksObtained() {
        return marksObtained;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public double getPercentage() {
        if (maxMarks == 0) return 0;
        return (marksObtained / maxMarks) * 100.0;
    }
}
