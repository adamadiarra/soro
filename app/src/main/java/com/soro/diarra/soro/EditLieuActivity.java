package com.soro.diarra.soro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditLieuActivity extends AppCompatActivity {
    private MapView mapView;
    private Button validBtn;
    private EditText editNomView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private float lat;
    private float lng;
    private String voyageId;
    private String lieuId;
    private ProgressBar progressBar;
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.token_key_map));
        setContentView(R.layout.activity_edit_lieu);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mapView = (MapView)findViewById(R.id.edit_map);
        editNomView = (EditText) findViewById(R.id.edit_nom_lieu);
        validBtn = (Button) findViewById(R.id.valid_edit_lieu);
        progressBar=(ProgressBar)findViewById(R.id.edit_progress);

        String nom = getIntent().getStringExtra("nom");
            lat = getIntent().getFloatExtra("lat",1);
            lng = getIntent().getFloatExtra("lng",1);
            voyageId = getIntent().getStringExtra("voyage_id");
            lieuId = getIntent().getStringExtra("lieu_id");


        editNomView.setText(nom);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                marker = new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .setTitle(nom);
                mapboxMap.addMarker(marker);


                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lat, lng)) // Sets the new camera position
                        .zoom(10) // Sets the zoom to level 10
                        .tilt(20) // Set the camera tilt to 20 degrees
                        .build(); // Builds the CameraPosition object from the builder
                mapboxMap.setCameraPosition(position);
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));


                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener(){
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        removeMarker(mapboxMap);
                        lat = (float) point.getLatitude();
                        lng = (float) point.getLongitude();
                        marker.setPosition(new LatLng(point));

                        mapboxMap.addMarker(marker);

                    }
                });
            }
        });


        validBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    updateLieu();
            }
        });

    }

    private void removeMarker(MapboxMap mapboxMap) {
        List<Marker> listMakers = mapboxMap.getMarkers();
        for (Marker marker : listMakers) {
            mapboxMap.removeMarker(marker);
        }
    }

    private void updateLieu() {

        boolean cancel = false;
        View focusView = null;

        editNomView.setError(null);

        String nomE = editNomView.getText().toString();

        if(!isValidLatlng(lat,lng)&&!TextUtils.isEmpty(nomE)){
            cancel=true;
            focusView=mapView;
            Toast.makeText(EditLieuActivity.this,"Position invalide",Toast.LENGTH_SHORT).show();
        }else if(!isValidNom(nomE)){
            cancel=true;
            focusView=editNomView;
            editNomView.setError("nom invalide");
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            editNomView.setEnabled(false);
            mapView.setEnabled(false);
            validBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            if (mAuth.getCurrentUser() != null) {
                Map<String, Object> updateval = new HashMap<>();
                updateval.put("latitude", lat);
                updateval.put("longitude", lng);
                updateval.put("name", nomE);

                firebaseFirestore.collection("Voyages/" + voyageId + "/lieux").document(lieuId).update(updateval)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    finish();
                                    Toast.makeText(EditLieuActivity.this, "update success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditLieuActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

            }
        }
    }


    private boolean isValidLatlng(float lat,float lng){
        return lat!=0&&lng!=0;
    }

    private boolean isValidNom(String nom){
        return nom.length()>4;
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
