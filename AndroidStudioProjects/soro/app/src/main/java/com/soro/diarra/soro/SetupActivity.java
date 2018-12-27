package com.soro.diarra.soro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupActivity extends AppCompatActivity {

    private Button saveBtn;
    private EditText UserNameView;
    private EditText UserBirthView;
    private AutoCompleteTextView countryView;
    private FloatingActionButton editBtn;
    private View setupViewer;
    private CircleImageView userImgView;
    private String user_id;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private Bitmap compressedImageFile;
    private Uri postImageUri = null;
    private Calendar myCalendar;
    private static final String[] paysData = (new PaysData()).lespays;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = mAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        UserNameView = (EditText) findViewById(R.id.user_name_view);
        UserBirthView = (EditText) findViewById(R.id.user_birth_day_view);
        countryView = (AutoCompleteTextView) findViewById(R.id.user_country_view);
        editBtn = (FloatingActionButton) findViewById(R.id.edit_setting_btn);
        saveBtn = (Button) findViewById(R.id.save_btn);
        setupViewer =  findViewById(R.id.contain_viewer);
        userImgView = (CircleImageView) findViewById(R.id.user_im_view);

        // les pays
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, paysData);
        countryView = (AutoCompleteTextView) findViewById(R.id.countries_list);
        countryView.setAdapter(adapter);

        UserBirthView.setFocusable(false);


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
        UserBirthView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SetupActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        userImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(200,200)
                        .setMaxCropResultSize(600,600)
                        .start(SetupActivity.this);
            }
        });

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().exists()){
                        DocumentSnapshot doc = task.getResult();
                        editBtn.show();
                        UserNameView.setText(doc.getString("name"));
                        UserBirthView.setText(doc.getString("birth"));
                        countryView.setText(doc.getString("country"));
                        postImageUri = Uri.parse(doc.getString("image"));
                        RequestOptions placeholderRequest = new RequestOptions();

                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(doc.getString("image")).into(userImgView);
                        enableForm(false);
                    }else {
                        editBtn.hide();
                        enableForm(true);
                    }
                }else {

                }

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableForm(true);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetup();
            }
        });


    }

    private void enableForm(boolean b) {
        if(b){
            UserBirthView.setEnabled(true);
            UserNameView.setEnabled(true);
            countryView.setEnabled(true);
            userImgView.setEnabled(true);
            saveBtn.setVisibility(View.VISIBLE);
        }else {
            UserBirthView.setEnabled(false);
            UserNameView.setEnabled(false);
            countryView.setEnabled(false);
            userImgView.setEnabled(false);
            saveBtn.setVisibility(View.INVISIBLE);
        }
    }


    private void saveSetup() {
        String nom = UserNameView.getText().toString();
        String birth = UserBirthView.getText().toString();
        String country = countryView.getText().toString();

        if(!TextUtils.isEmpty(nom)&& postImageUri != null&&!TextUtils.isEmpty(birth)&&!TextUtils.isEmpty(country)){
            enableForm(false);
            final String randomName = UUID.randomUUID().toString();
            File newImageFile = new File(postImageUri.getPath());

            try {

                compressedImageFile = new Compressor(SetupActivity.this)
                        .setMaxHeight(600)
                        .setMaxWidth(600)
                        .setQuality(50)
                        .compressToBitmap(newImageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            final Bitmap bitmap = ((BitmapDrawable) userImgView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            final StorageReference postStorage = storageReference.child("users").child(randomName+".jpg");
            UploadTask uploadTask = postStorage.putBytes(imageData);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return postStorage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    final String downloadUri_post = task.getResult().toString();
                    if(task.isSuccessful()){
                        File newThumbFile = new File(postImageUri.getPath());
                        try {

                            compressedImageFile = new Compressor(SetupActivity.this)
                                    .setMaxHeight(100)
                                    .setMaxWidth(100)
                                    .setQuality(1)
                                    .compressToBitmap(newThumbFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                        UploadTask uploadTask = storageReference.child("users/thumbs")
                                .child(randomName + ".jpg").putBytes(thumbData);

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return postStorage.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String downloadthumbUri = task.getResult().toString();
                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("name", nom);
                                postMap.put("image", downloadUri_post);
                                postMap.put("birth",birth);
                                postMap.put("country",country);

                                firebaseFirestore.collection("Users").document(user_id).set(postMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   gotoMain();
                                               }
                                            }
                                        });
                            }
                        });

                    }

                }
            });
        }

    }

    private void gotoMain() {
        Intent it = new Intent(SetupActivity.this,MainActivity.class);
        startActivity(it);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        UserBirthView.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                userImgView.setImageURI(postImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
