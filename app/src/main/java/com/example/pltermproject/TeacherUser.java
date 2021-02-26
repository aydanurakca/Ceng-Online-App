package com.example.pltermproject;

public class TeacherUser extends User {

    private String academicRank;
    private String phone;


    public TeacherUser(String academicRank, String phone, String name, String email, String userType) {
        super(name, email, userType);
        this.academicRank = academicRank;
        this.phone = phone;
    }

    public String getAcademicRank() {
        return academicRank;
    }

    public void setAcademicRank(String academicRank) {
        this.academicRank = academicRank;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
