package com.soro.diarra.soro;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class VoyageFragment extends Fragment {


    private String user_id;

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    FirebaseFirestore firebaseFirestore;

    private RecyclerView listeView;
    private FloatingActionButton addNewVoyagesbtn;

    private List<Voyage> list_voyages;
    private VoyageReclycleAdapter voyageReclycleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voyage, container, false);

        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        list_voyages = new ArrayList<>();
        voyageReclycleAdapter = new VoyageReclycleAdapter(list_voyages);


        listeView = view.findViewById(R.id.list_voyage_view);
        listeView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listeView.setAdapter(voyageReclycleAdapter);
        listeView.setAdapter(voyageReclycleAdapter);

        // add new voyage
        addNewVoyagesbtn = view.findViewById(R.id.add_new_voyage);
        addNewVoyagesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NewVoyageActivity.class);
                startActivity(intent);
            }
        });

        if(mAuth.getCurrentUser()!=null) {
            Query firstQuery = firebaseFirestore.collection("Voyages").orderBy("titre", Query.Direction.DESCENDING);

            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            user_id = mAuth.getCurrentUser().getUid();
                            String voyageId = doc.getDocument().getId();
                            Voyage voyage = doc.getDocument().toObject(Voyage.class).withId(voyageId);
                            //Toast.makeText(getApplicationContext(),voyage.getUser_id()+" = "+user_id,Toast.LENGTH_LONG).show();
                            if (user_id.equals(voyage.getUser_id())) {
                                list_voyages.add(voyage);
                                voyageReclycleAdapter.notifyDataSetChanged();
                            }


                        }
                    }
                }

            });
        }

        return view;
    }



}
