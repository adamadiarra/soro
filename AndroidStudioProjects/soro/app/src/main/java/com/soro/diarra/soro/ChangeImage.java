package com.soro.diarra.soro;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChangeImage extends Thread {
    private int  index = 0;
    private boolean mRun = false;
    private ImageView imageView;
    private Context context;

    long changeInterval;
    List<String> images;

    public ChangeImage(Context context,ImageView imageView,List<String> images,long changeInterval){
        this.imageView = imageView;
        this.images = images;
        this.changeInterval = changeInterval;
        this.context = context;
    }

    @Override
    public void run() {
        while (mRun) {
            if (index >= images.size()) {
                index = 0;
            }

            synchronized(imageView) {
                Glide.with(context).load(images.get(index++)).into(imageView);
            }

            try {
                sleep(changeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startShow(){
        mRun = true;
    }

    public void stopShow() {
        mRun = false;
    }
}
