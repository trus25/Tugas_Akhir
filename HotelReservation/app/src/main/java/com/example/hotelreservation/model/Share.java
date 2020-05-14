package com.example.hotelreservation.model;

public class Share {
    private String no;
    private String sid;
    private String uid;
    private String username;

    public Share(String no, String sid, String uid, String username) {
        this.no = no;
        this.sid = sid;
        this.uid = uid;
        this.username = username;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return no+".    "+username;
    }
}
