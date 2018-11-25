package com.soro.diarra.soro;


public class Voyage extends VoyageId  {

    private String titre;

    private String user_id;
    private String date;





    public Voyage() {
    }

    public Voyage(String titre, String date,String user_id) {
        this.titre = titre;
        this.user_id = user_id;
        this.date = date;

    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
