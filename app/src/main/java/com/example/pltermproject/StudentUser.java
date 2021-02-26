package com.example.pltermproject;

public class StudentUser extends User{

    private String GPA;
    private String studentNumber;
    private String grade;


    public StudentUser(String GPA, String studentNumber, String grade, String name, String email, String userType) {
        super(name, email, userType);
        this.GPA = GPA;
        this.studentNumber = studentNumber;
        this.grade = grade;
    }

    public String getGPA() {
        return GPA;
    }

    public void setGPA(String GPA) {
        this.GPA = GPA;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
