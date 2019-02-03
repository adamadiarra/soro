package com.soro.diarra.soro;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment implements UserRecycleAdapter.UserAdapterListener{

    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private List<User> users;
    private UserRecycleAdapter adapter;
    private RecyclerView listView;

    private FirebaseAuth mAuth;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_request, container, false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        adapter = new UserRecycleAdapter(getActivity(),users,this);

        listView = view.findViewById(R.id.req_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(adapter);

        firebaseFirestore.collection("Users/"+user_id+"/friends").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                        String friendId = doc.getDocument().getId();
                        String requete = doc.getDocument().getString("requete");
                        if(requete.equals("receive")){
                            firebaseFirestore.collection("Users").document(friendId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        User user = task.getResult().toObject(User.class).withId(friendId);
                                        users.add(user);
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
