package com.example.hotelreservation.model;

import android.os.Parcelable;

public class Hotel{
    private String id;
    private String nama;
    private String alamat;
    private String imagepath;
    private String harga;

    public Hotel(String id, String nama, String alamat, String imagepath, String harga) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.imagepath = imagepath;
        this.harga = harga;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }
}
