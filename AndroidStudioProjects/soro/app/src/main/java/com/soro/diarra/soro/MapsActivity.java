package com.soro.diarra.soro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MapsActivity extends AppCompatActivity {

    private MapView mapView;
    private ImageView im;
    String voyageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.token_key_map));
        setContentView(R.layout.activity_maps);

        voyageId = getIntent().getStringExtra("voyage_id");
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_map_l);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lieu de voyage");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent it = new Intent(MapsActivity.this,LieuxActivity.class);
        it.putExtra("voyage_id",voyageId);

        im = (ImageView) findViewById(R.id.map_lieu_img);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        final float lat = getIntent().getFloatExtra("lat",1);
        final float lng = getIntent().getFloatExtra("lng",1);
        final String titre = getIntent().getStringExtra("nom");
        final String uri = getIntent().getStringExtra("uri");


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(titre)
                );


                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lat, lng)) // Sets the new camera position
                        .zoom(15) // Sets the zoom to level 10
                        .tilt(20) // Set the camera tilt to 20 degrees
                        .build(); // Builds the CameraPosition object from the builder
                mapboxMap.setCameraPosition(position);
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                Glide.with(getApplicationContext()).load(uri).into(im);

                im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(MapsActivity.this,FullScreenActivity.class);
                        it.putExtra("uri",uri);
                        startActivity(it);
                    }
                });


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
        Intent it = new Intent(MapsActivity.this,LieuxActivity.class);
        it.putExtra("voyage_id",voyageId);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
