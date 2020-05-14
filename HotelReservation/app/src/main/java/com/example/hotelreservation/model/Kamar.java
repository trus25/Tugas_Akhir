package com.example.hotelreservation.model;

public class Kamar {
    private String id;
    private String nomor;

    public Kamar(String id, String nomor) {
        this.id = id;
        this.nomor = nomor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    @Override
    public String toString() {
        return nomor;
    }
}
