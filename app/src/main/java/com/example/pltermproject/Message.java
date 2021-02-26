package com.example.pltermproject;

import java.util.Date;

public class Message {
    String sender;
    String receiver;
    Date date;
    String subject;
    String message;
    String receiverName;
    String senderName;

    public Message(String sender, String receiver, Date date, String subject, String message, String receiverName, String senderName) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.subject = subject;
        this.message = message;
        this.receiverName = receiverName;
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
