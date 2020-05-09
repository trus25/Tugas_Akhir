package com.example.hotelreservation.model;

public class Data {
    private String namadata;
    private String postData;

    public Data(String namadata, String postData) {
        this.namadata = namadata;
        this.postData = postData;
    }

    public String getNamadata() {
        return namadata;
    }

    public void setNamadata(String namadata) {
        this.namadata = namadata;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }
}
