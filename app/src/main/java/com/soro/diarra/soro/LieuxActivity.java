package com.soro.diarra.soro;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class LieuxActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView listView;
    private List<Lieu> lieux;

    private LieuRecycleAdapter lieuRecycleAdapter;
    private String voyageId;
    private FloatingActionButton addLieuBtn;

    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lieux);

        voyageId = getIntent().getStringExtra("voyage_id");
        firebaseFirestore = FirebaseFirestore.getInstance();

        toolbar = (Toolbar)findViewById(R.id.lieutoolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore.collection("Voyages").document(voyageId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    getSupportActionBar().setTitle("Lieux de voyage "+task.getResult().getString("titre"));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);

                }
            }
        });






        addLieuBtn = (FloatingActionButton) findViewById(R.id.lieu_float_button);


        lieux = new ArrayList<>();
        listView = (RecyclerView)findViewById(R.id.list_lieux);
        lieuRecycleAdapter = new LieuRecycleAdapter(lieux,voyageId);

        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(lieuRecycleAdapter);

        addLieuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(LieuxActivity.this,NewLocationActivity.class);
                intent.putExtra("voyage_id",voyageId);
                startActivity(intent);
            }
        });

    }

    private void loadLieux() {
        lieux.clear();
        lieuRecycleAdapter.notifyDataSetChanged();
        firebaseFirestore.collection("Voyages/"+voyageId+"/lieux")
                .orderBy("name",Query.Direction.DESCENDING).addSnapshotListener(LieuxActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                        if(doc.getType() == DocumentChange.Type.ADDED){

                            String lieuId = doc.getDocument().getId();
                            String name = doc.getDocument().getString("name");
                            String image = doc.getDocument().getString("image");
                            String dateTime = doc.getDocument().getString("date_time");
                            double lat = doc.getDocument().getDouble("latitude");
                            double lng = doc.getDocument().getDouble("longitude");

                            Lieu lieu = new Lieu(name,image,(float) lat,(float) lng,dateTime);
                            lieu.setId(lieuId);
                            lieux.add(lieu);
                            lieuRecycleAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLieux();

    }




}
