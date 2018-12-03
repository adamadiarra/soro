package com.soro.diarra.soro;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class VoyageMapActivity extends AppCompatActivity {

    private MapView mapView;
    private FirebaseFirestore firebaseFirestore;
    private List<String> uris;
    private ImageAdapter imageAdapter;
    private RecyclerView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.token_key_map));
        setContentView(R.layout.activity_voyage_map);


        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_map_v);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lieux de voyages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uris = new ArrayList<>();
        imageAdapter = new ImageAdapter(uris);
        listView = (RecyclerView) findViewById(R.id.list_im_map);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(imageAdapter);

        firebaseFirestore=FirebaseFirestore.getInstance();

        mapView = (MapView) findViewById(R.id.mapViewVoyage);
        mapView.onCreate(savedInstanceState);



        final String voyagesId = getIntent().getStringExtra("voyage_id");


        final float lat = getIntent().getFloatExtra("lat",1);
        final float lng = getIntent().getFloatExtra("lng",1);
        final String titre = getIntent().getStringExtra("nom");
        final String uri = getIntent().getStringExtra("uri");

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

            }
        });



        Query lieuxQuery = firebaseFirestore.collection("Voyages/"+voyagesId+"/lieux");
        lieuxQuery.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                        double lat = doc.getDocument().getDouble("latitude");
                        double lng = doc.getDocument().getDouble("longitude");
                        String titre = doc.getDocument().getString("name");
                        String uri = doc.getDocument().getString("image");
                        Uri url = Uri.parse(uri);

                        uris.add(uri);
                        imageAdapter.notifyDataSetChanged();

                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(MapboxMap mapboxMap) {
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lng))
                                        .title(titre)
                                );

                                CameraPosition position = new CameraPosition.Builder()
                                        .target(new LatLng(lat, lng)) // Sets the new camera position
                                        .zoom(5) // Sets the zoom to level 10
                                        .tilt(20) // Set the camera tilt to 20 degrees
                                        .build(); // Builds the CameraPosition object from the builder
                                mapboxMap.setCameraPosition(position);
                                mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));

                            }
                        });

                    }
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
