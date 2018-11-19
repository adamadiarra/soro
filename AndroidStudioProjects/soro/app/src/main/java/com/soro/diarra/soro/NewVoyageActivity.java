package com.soro.diarra.soro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class NewVoyageActivity extends AppCompatActivity {
    private EditText edittitreView;
    private EditText editdateView;
    private Button addNewVoyageBtn;
    private ProgressBar newVoyageProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_voyage);

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
        if(!TextUtils.isEmpty(titre)){
            edittitreView.setError("Titre est vide");
            focusView = edittitreView;
            cancel = true;
        }else if(!isValidateTitre(titre)){
            edittitreView.setError("Titre doit être > 4 ");
            focusView = edittitreView;
            cancel = true;
        }
        if(!TextUtils.isEmpty(date)){
            editdateView.setError(" la date manquante");
            focusView = editdateView;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            newVoyageProgressBar.setVisibility(View.VISIBLE);

            //todo
            /**
             * firebase adding voyage;
             * if success add lieux
             */
        }
    }

    private boolean isValidateTitre(String titre) {
        return titre.length()>3;
    }
}
