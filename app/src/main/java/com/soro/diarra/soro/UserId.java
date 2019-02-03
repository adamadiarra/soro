package com.soro.diarra.soro;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

class UserId {
    @Exclude
    public String UserId;
    public <T extends UserId> T withId(@NonNull final String id){
        this.UserId = id;
        return (T)this;
    }
}
