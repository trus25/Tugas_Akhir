package com.example.hotelreservation.model;

public class Myroom {
    private String idtr;
    private String idusr;
    private String idht;
    private String idkmr;
    private String tanggal;
    private String checkin;
    private String checkout;
    private String checkinstats;
    private String nama;
    private String alamat;
    private String imagepath;

    public Myroom(String idtr, String idusr, String idht, String idkmr, String tanggal, String checkin, String checkout, String checkinstats, String nama, String alamat, String imagepath) {
        this.idtr = idtr;
        this.idusr = idusr;
        this.idht = idht;
        this.idkmr = idkmr;
        this.tanggal = tanggal;
        this.checkin = checkin;
        this.checkout = checkout;
        this.checkinstats = checkinstats;
        this.nama = nama;
        this.alamat = alamat;
        this.imagepath = imagepath;
    }

    public String getIdtr() {
        return idtr;
    }

    public void setIdtr(String idtr) {
        this.idtr = idtr;
    }

    public String getIdusr() {
        return idusr;
    }

    public void setIdusr(String idusr) {
        this.idusr = idusr;
    }

    public String getIdht() {
        return idht;
    }

    public void setIdht(String idht) {
        this.idht = idht;
    }

    public String getIdkmr() {
        return idkmr;
    }

    public void setIdkmr(String idkmr) {
        this.idkmr = idkmr;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public String getCheckinstats() {
        return checkinstats;
    }

    public void setCheckinstats(String checkinstats) {
        this.checkinstats = checkinstats;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
