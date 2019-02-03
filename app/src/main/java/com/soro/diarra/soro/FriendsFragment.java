package com.soro.diarra.soro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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


public class FriendsFragment extends Fragment implements UserRecycleAdapter.UserAdapterListener{

    private RecyclerView listView;
    private List<User> friends;
    private EditText searchView;
    private UserRecycleAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String user_id;
    public FriendsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = view.findViewById(R.id.friends_list);
        friends = new ArrayList<>();
        adapter = new UserRecycleAdapter(getActivity(),friends,this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Query query = firebaseFirestore.collection("Users/"+user_id+"/friends");
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                        if(doc.getType()==DocumentChange.Type.ADDED){
                            String f_id = doc.getDocument().getId();
                            Query query1 = firebaseFirestore.collection("Users/"+f_id);
                            query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                                        if(doc.getType()==DocumentChange.Type.ADDED){
                                            User user = doc.getDocument().toObject(User.class).withId(f_id);
                                            friends.add(user);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onUserSelected(User user) {

    }
}
