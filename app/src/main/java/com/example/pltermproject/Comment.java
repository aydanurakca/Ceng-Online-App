package com.example.pltermproject;

import java.util.Date;

public class Comment {

    private String sender;
    private String comment;
    private Date date;
    private String image;


    public Comment(String sender, String comment, Date date, String image) {
        this.sender = sender;
        this.comment = comment;
        this.date = date;
        this.image = image;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
