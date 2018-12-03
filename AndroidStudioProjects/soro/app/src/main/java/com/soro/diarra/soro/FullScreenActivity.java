package com.soro.diarra.soro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenActivity extends AppCompatActivity {
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        Toolbar tb = (Toolbar)findViewById(R.id.lieutoolbar_full);
        setSupportActionBar(tb);

        imageView = (ImageView)findViewById(R.id.full_im);

        String uri = getIntent().getStringExtra("uri");
        if(uri!=null) {
            Glide.with(FullScreenActivity.this).load(uri).into(imageView);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        imageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setAdjustViewBounds(false);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               finish();

            }
        });

    }
}
