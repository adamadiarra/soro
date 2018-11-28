package com.soro.diarra.soro;

public class Lieu {
    private String nom;
    private String image;
    private float latitude,longitude;

    public Lieu(String nom, String image, float latitude,float longitude) {
        this.nom = nom;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float lat) {
        this.latitude = lat;
    }

    public float getLonitude() {
        return longitude;
    }

    public void setLonitude(float lng) {
        this.longitude = lng;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
