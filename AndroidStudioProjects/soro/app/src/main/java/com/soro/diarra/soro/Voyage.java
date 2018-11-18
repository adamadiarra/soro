package com.soro.diarra.soro;

import java.util.Date;
import java.util.List;

public class Voyage {

    private String titre;
    private Date date;





    private List<Lieu> lieux;

    public Voyage() {
    }

    public Voyage(String titre, Date date, List<Lieu> lieux) {
        this.titre = titre;
        this.date = date;
        this.lieux = lieux;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Lieu> getLieux() {
        return lieux;
    }

    public void setLieux(List<Lieu> lieux) {
        this.lieux = lieux;
    }

    public void addLieu(Lieu lieu) {
        this.lieux.add(lieu);
    }

    public String getImage() {
        if(lieux!=null){
            return lieux.get(0).getImage();
        }
        return "";
    }

}
