package com.soro.diarra.soro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button decBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        mAuth = FirebaseAuth.getInstance();
        decBtn = (Button)findViewById(R.id.logout_btn);
        cancelBtn = (Button)findViewById(R.id.annul_logout);

        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null){
                    mAuth.signOut();
                    Intent intent = new Intent(LogoutActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
       cancelBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(LogoutActivity.this,MainActivity.class);
               startActivity(intent);
               finish();
           }
       });

    }

}
