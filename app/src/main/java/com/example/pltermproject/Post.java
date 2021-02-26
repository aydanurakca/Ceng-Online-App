package com.example.pltermproject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Post {

    private Date date;
    private String postedBy;
    private String post;
    private ArrayList comments;
    private String course;
    private String image;

    public Post(Date date, String postedBy, String post, ArrayList comments, String course, String image) {
        this.date = date;
        this.postedBy = postedBy;
        this.post = post;
        this.comments = comments;
        this.course = course;
        this.image = image;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPost() { return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public ArrayList getComments() {
        return comments;
    }

    public void setComments(ArrayList comments) {
        this.comments = comments;
    }
}
