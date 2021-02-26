package com.example.pltermproject;

import java.util.Map;

public class Assignment {

    private String description;
    private String dueDate;
    private String name;
    private String course;
    private Map<String, String> uploads;

    public Assignment(String description, String dueDate, String name, Map<String, String> uploads, String course) {
        this.description = description;
        this.dueDate = dueDate;
        this.name = name;
        this.uploads = uploads;
        this.course = course;
    }


    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Map<String, String> getUploads() {
        return uploads;
    }

    public void setUploads(Map<String, String> uploads) {
        this.uploads = uploads;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
