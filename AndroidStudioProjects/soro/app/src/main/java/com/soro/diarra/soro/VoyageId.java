package com.soro.diarra.soro;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class VoyageId {

    @Exclude
    public String VoyageId;
    public <T extends VoyageId> T withId(@NonNull final String id){
        this.VoyageId = id;
        return (T)this;
    }
}
