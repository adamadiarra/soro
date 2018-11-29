package com.soro.diarra.soro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewVoyageActivity extends AppCompatActivity {
    private EditText edittitreView;
    private EditText editdateView;
    private Button addNewVoyageBtn;
    AutoCompleteTextView textView;
    private ProgressBar newVoyageProgressBar;

    private String user_id;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private Calendar myCalendar ;
    private static final String[] paysData = (new PaysData()).lespays;


    @RequiresApi(api = Build.VERSION_CODES.N)
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
        editdateView.setFocusable(false);

        // les pays
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, paysData);
                textView = (AutoCompleteTextView)
                findViewById(R.id.countries_list);
        textView.setAdapter(adapter);



        addNewVoyageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempVoyage();
            }
        });


        //date picker
        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        editdateView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NewVoyageActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void attempVoyage() {

        //todo
        /**
         * check user connexion
         */

        boolean cancel = false;
        View focusView = null;

        String titre = edittitreView.getText().toString();
        String date = editdateView.getText().toString();
        String pays = textView.getText().toString();

        if(TextUtils.isEmpty(pays)){
            textView.setError("Pays est vide");
            focusView = textView;
            cancel = true;
        }else if(!stringContainsItemFromList(pays,paysData)){
            textView.setError("Pays non valide");
            focusView = textView;
            cancel = true;
        }
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
            titre = titre+"("+pays+")";
            //store voyage
            Map<String,Object> voyageMap = new HashMap();
            voyageMap.put("titre",titre);
            voyageMap.put("date",date);
            voyageMap.put("user_id",user_id);
            firebaseFirestore.collection("Voyages").add(voyageMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"voyage added",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NewVoyageActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                    newVoyageProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private boolean isValidateTitre(String titre) {
        return titre.length()>3;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        editdateView.setText(sdf.format(myCalendar.getTime()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }
}
