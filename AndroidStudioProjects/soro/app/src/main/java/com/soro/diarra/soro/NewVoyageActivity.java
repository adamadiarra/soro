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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewVoyageActivity extends AppCompatActivity {
    private EditText edittitreView;
    private EditText editdateView;
    private Button addNewVoyageBtn;
    private ProgressBar newVoyageProgressBar;

    private String user_id;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_voyage);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();


        newVoyageProgressBar = (ProgressBar)findViewById(R.id.new_voyage_progress);

        editdateView = (EditText)findViewById(R.id.new_voyage_date);
        edittitreView = (EditText)findViewById(R.id.new_voyage_titre);
        addNewVoyageBtn = (Button) findViewById(R.id.new_voyage_btn);

        addNewVoyageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempVoyage();
            }
        });


    }

    private void attempVoyage() {

        //todo
        /**
         * check user connexion
         */

        boolean cancel = false;
        View focusView = null;

        String titre = edittitreView.getText().toString();
        String date = editdateView.getText().toString();
        if(TextUtils.isEmpty(titre)){
            edittitreView.setError("Titre est vide");
            focusView = edittitreView;
            cancel = true;
        }else if(!isValidateTitre(titre)){
            edittitreView.setError("Titre doit être > 4 ");
            focusView = edittitreView;
            cancel = true;
        }
        if(TextUtils.isEmpty(date)){
            editdateView.setError(" la date manquante");
            focusView = editdateView;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            newVoyageProgressBar.setVisibility(View.VISIBLE);

            //store voyage
            Map<String,Object> voyageMap = new HashMap();
            voyageMap.put("titre",titre);
            voyageMap.put("date",date);
            firebaseFirestore.collection("Voyages").document(user_id)
                    .set(voyageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(NewVoyageActivity.this, NewLocationActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean isValidateTitre(String titre) {
        return titre.length()>3;
    }
}
