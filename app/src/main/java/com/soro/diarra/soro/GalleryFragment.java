package com.soro.diarra.soro;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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

public class GalleryFragment extends Fragment {
    private String user_id;

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    FirebaseFirestore firebaseFirestore;

    private RecyclerView listeView;
    private GallryRecycleView gallryRecycleViewAdapter;

    private List<Lieu> lieuList;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        lieuList = new ArrayList<>();
        gallryRecycleViewAdapter = new GallryRecycleView(lieuList);
        listeView = view.findViewById(R.id.gallery_list_view);
        listeView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        listeView.setAdapter(gallryRecycleViewAdapter);


        //firebase
        if(mAuth.getCurrentUser()!=null) {
            ArrayList<String> voyageIds = new ArrayList<>();
            Query voyagesQuery = firebaseFirestore.collection("Voyages");
            voyagesQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshots!=null){
                        for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                user_id = mAuth.getCurrentUser().getUid();
                                String voyageId = doc.getDocument().getId();
                                Voyage voyage = doc.getDocument().toObject(Voyage.class).withId(voyageId);
                                if (user_id.equals(voyage.getUser_id())){



                                    firebaseFirestore.collection("Voyages/"+voyageId+"/lieux")
                                            .orderBy("name",Query.Direction.DESCENDING).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if(!documentSnapshots.isEmpty()){
                                                for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                                                    if(doc.getType() == DocumentChange.Type.ADDED){

                                                        String lieuId = doc.getDocument().getId();
                                                        String name = doc.getDocument().getString("name");
                                                        String image = doc.getDocument().getString("image");
                                                        double lat = doc.getDocument().getDouble("latitude");
                                                        double lng = doc.getDocument().getDouble("longitude");
                                                        String dateTime = doc.getDocument().getString("date_time");
                                                        Lieu lieu = new Lieu(name,image,(float) lat,(float) lng,dateTime);
                                                        lieu.setId(lieuId);
                                                        lieu.setVoyageId(voyageId);
                                                        lieuList.add(lieu);
                                                        gallryRecycleViewAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }

                        }
                    }
                }
            });
        }
        return view;
    }

}
