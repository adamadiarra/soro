package com.soro.diarra.soro;

public class Lieu {
    private String nom;
    private String image;
    private float latitude,longitude;


    private String id;
    private String voyageId;
    private String date_time;

    public Lieu(String nom, String image, float latitude,float longitude,String date_time) {
        this.nom = nom;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date_time=date_time;
    }

    public String getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(String voyageId) {
        this.voyageId = voyageId;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}
