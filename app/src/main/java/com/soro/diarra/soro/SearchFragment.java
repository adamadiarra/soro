package com.soro.diarra.soro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements UserRecycleAdapter.UserAdapterListener{
    private EditText searchView;
    private RecyclerView listview;
    private UserRecycleAdapter adapter;
    private List<User> users;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        listview = view.findViewById(R.id.list_user);
        listview.setLayoutManager(new LinearLayoutManager(getActivity()));

        users = new ArrayList<>();
        adapter = new UserRecycleAdapter(getActivity(),users,SearchFragment.this);
        listview.setAdapter(adapter);
        // Inflate the layout for this fragment
        searchView = view.findViewById(R.id.search_view);


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textinput = s.toString();
                fetchUser(textinput);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void fetchUser(String newText) {
        users.clear();
        Query query = firebaseFirestore.collection("Users").orderBy("name",Query.Direction.ASCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String userId = doc.getDocument().getId();
                            User user = doc.getDocument().toObject(User.class).withId(userId);
                            if(user.getName().toLowerCase().contains(newText))
                            users.add(user);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onUserSelected(User user) {
        String userID = user.UserId;
        Toast.makeText(getActivity(),userID,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),SetupActivity.class);
        intent.putExtra("user_id",userID);
        startActivity(intent);
    }
}
