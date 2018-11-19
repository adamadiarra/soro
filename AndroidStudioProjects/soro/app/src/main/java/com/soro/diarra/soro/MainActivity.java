package com.soro.diarra.soro;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView listeView;
    private List<Voyage> list_voyages;
    private VoyageReclycleAdapter voyageReclycleAdapter;
    private FloatingActionButton addNewVoyagesbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_voyages = new ArrayList<>();
        voyageReclycleAdapter = new VoyageReclycleAdapter(list_voyages);
        listeView = (RecyclerView)findViewById(R.id.voyage_contain);
        listeView.setLayoutManager(new LinearLayoutManager(this));
        listeView.setAdapter(voyageReclycleAdapter);

        // add new voyage
        addNewVoyagesbtn = (FloatingActionButton)findViewById(R.id.add_new_voyage);
        addNewVoyagesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NewVoyageActivity.class);
                startActivity(intent);
            }
        });



        //todo
        /**
         * get voyages from firebase and update;
         */
    }
}
