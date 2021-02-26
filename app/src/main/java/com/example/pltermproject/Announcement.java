package com.example.pltermproject;

public class Announcement {

    private String subject;
    private String savedBy;
    private String modifiedDate;
    private String course;
    private String description;


    public Announcement(String subject, String savedBy, String modifiedDate, String course, String description) {
        this.subject = subject;
        this.savedBy = savedBy;
        this.modifiedDate = modifiedDate;
        this.course = course;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
