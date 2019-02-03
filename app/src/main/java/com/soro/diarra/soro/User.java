package com.soro.diarra.soro;

public class User extends UserId{
    String name,image,country;

    public User() {
    }

    public User(String name, String image, String country) {
        this.name = name;
        this.image = image;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
