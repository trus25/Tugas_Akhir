package com.example.hotelreservation.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String nama;
    private String email;
    private String phone;

    public User(int id, String username, String password, String nama, String email, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nama = nama;
        this.email = email;
        this.phone = phone;
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
