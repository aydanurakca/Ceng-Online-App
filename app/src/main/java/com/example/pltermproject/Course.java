package com.example.pltermproject;

import java.util.ArrayList;

public class Course {


    private String code;
    private String grade;
    private String labteacher;
    private String name;
    private String sections;
    private String teacher;
    private String term;
    public ArrayList<String>students;
    private ArrayList<String>assignments;
    private ArrayList<String>announcements;

    Course(String code, String teacher, String grade, String name, String term, String labteacher, String sections, ArrayList<String> students, ArrayList<String>assignments, ArrayList<String> announcements) {
        this.code = code;
        this.grade = grade;
        this.labteacher = labteacher;
        this.name = name;
        this.sections = sections;
        this.teacher = teacher;
        this.term = term;
        this.students = students;
        this.assignments = assignments;
        this.announcements = announcements;
    }
    Course(){

    }

    public ArrayList<String> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(ArrayList<String> announcements) {
        this.announcements = announcements;
    }
    public ArrayList<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(ArrayList<String> assignments) {
        this.assignments = assignments;
    }

    public ArrayList<String> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<String> students) {
        this.students = students;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getLabteacher() {
        return labteacher;
    }

    public void setLabteacher(String labteacher) {
        this.labteacher = labteacher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSections() {
        return sections;
    }

    public void setSections(String sections) {
        this.sections = sections;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
