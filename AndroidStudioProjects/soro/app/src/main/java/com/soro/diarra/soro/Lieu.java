package com.soro.diarra.soro;

public class Lieu {
    private String nom;
    private String image;
    private LongLat position;

    public Lieu(String nom, String image, LongLat position) {
        this.nom = nom;
        this.image = image;
        this.position = position;
    }

    public Lieu(String nom, LongLat position) {
        this.nom = nom;
        this.position = position;
    }

    public Lieu(String image) {
        this.image = image;
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

    public LongLat getPosition() {
        return position;
    }

    public void setPosition(LongLat position) {
        this.position = position;
    }


}
