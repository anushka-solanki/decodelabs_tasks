package com.gradecalculator.model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String name;
    private String rollNumber;
    private String course;
    private String semester;
    private List<Subject> subjects;

    public Student(String name, String rollNumber, String course, String semester) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.course = course;
        this.semester = semester;
        this.subjects = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getCourse() {
        return course;
    }

    public String getSemester() {
        return semester;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }
}
