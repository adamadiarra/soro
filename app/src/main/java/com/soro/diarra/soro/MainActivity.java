package com.soro.diarra.soro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {


    private String user_id;

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    FirebaseFirestore firebaseFirestore;
    MaterialSearchView searchView;

    //fragments
    private VoyageFragment voyageFragment;
    private GalleryFragment galleryFragment;
    private SearchFragment searchFragment;

    private BottomNavigationView mainBottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar)findViewById(R.id.main_tollbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mes voyages");

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mainBottomNav= (BottomNavigationView)findViewById(R.id.main_btm_nav);

        //fragments
        if(mAuth.getCurrentUser() != null) {
            voyageFragment = new VoyageFragment();
            galleryFragment = new GalleryFragment();
            searchFragment = new SearchFragment();


            initializeFragment();

            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.voyage_nav:
                            replaceFragment(voyageFragment);
                            return true;
                        case R.id.gallery_nav:
                            replaceFragment(galleryFragment);
                            return true;
                        case R.id.action_search_nav:
                            replaceFragment(searchFragment);
                            return true;
                        default:
                            return false;
                    }
                }
            });

        }



    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        if(fragment == voyageFragment){
            fragmentTransaction.hide(galleryFragment);
            fragmentTransaction.hide(searchFragment);
        }

        if(fragment == galleryFragment){

            fragmentTransaction.hide(voyageFragment);
            fragmentTransaction.hide(searchFragment);
        }
        if(fragment == searchFragment){

            fragmentTransaction.hide(voyageFragment);
            fragmentTransaction.hide(galleryFragment);
        }

        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }


    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, voyageFragment);
        fragmentTransaction.add(R.id.main_container, galleryFragment);
        fragmentTransaction.add(R.id.main_container, searchFragment);
        fragmentTransaction.hide(searchFragment);
        fragmentTransaction.hide(galleryFragment);

        fragmentTransaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user==null){
            sendToLogin();
        }else {

            user_id = mAuth.getUid();

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
            });
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
        Intent intent = new Intent(MainActivity.this,LogoutActivity.class);
        startActivity(intent);
        finish();
    }


    private void sendToLogin() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }


}
