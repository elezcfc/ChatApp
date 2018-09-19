package com.example.elezs.chatapp.domen;

import java.util.Calendar;

public class Message {
    private String text;
    private String user;
    private long time;

    public Message(String text, String user) {
        this.text = text;
        this.user = user;
        time = Calendar.getInstance().getTimeInMillis();
    }

    public Message() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return user+": "+text;
    }
}
