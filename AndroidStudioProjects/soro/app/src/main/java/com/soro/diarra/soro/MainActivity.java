package com.soro.diarra.soro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private String user_id;

    private FirebaseAuth mAuth;
    private Toolbar toolbar;


    private RecyclerView listeView;
    private List<Voyage> list_voyages;
    private VoyageReclycleAdapter voyageReclycleAdapter;
    private FloatingActionButton addNewVoyagesbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.main_tollbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mon blog");

        mAuth = FirebaseAuth.getInstance();

        list_voyages = new ArrayList<>();
        voyageReclycleAdapter = new VoyageReclycleAdapter(list_voyages);

        listeView = (RecyclerView)findViewById(R.id.list_voyage_view);

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


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user==null){
            sendToLogin();
        }else {

            user_id = mAuth.getUid();
            /**
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent it = new Intent(MainActivity.this,SetupActivity.class);
                            startActivity(it);
                            finish();

                        }

                    }else {

                        Toast.makeText(MainActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
            });*/
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_set_btn:
                Intent intent = new Intent(MainActivity.this,SetupActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }


    private void sendToLogin() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }
}
