package com.soro.diarra.soro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static android.support.media.ExifInterface.*;
import static android.support.media.ExifInterface.TAG_GPS_LATITUDE;

public class NewLocationActivity extends AppCompatActivity {

    private ImageView imgLocation;
    private EditText editNamneLocation;
    private Button newLieuBtn;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    ExifInterface exifInterface;
    private FloatingActionButton cameraBtn;

    String voyageId;

    private Bitmap compressedImageFile;
    private Uri postImageUri = null;
    private String user_id = null;
    float[] position = new float[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        voyageId = getIntent().getStringExtra("voyage_id");

        Log.i("voyageidddddd",voyageId);
        imgLocation = (ImageView) findViewById(R.id.new_lieu_img);
        cameraBtn = (FloatingActionButton)findViewById(R.id.camea_action);

        editNamneLocation = (EditText) findViewById(R.id.new_lieu_name);
        newLieuBtn = (Button) findViewById(R.id.new_lieu_btn);

        //cropping image
        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(500,500)
                        .setMaxCropResultSize(5000,5000)
                        .start(NewLocationActivity.this);**/

                Intent intent= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });
        //camera button
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takephoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takephoto,0);
            }
        });

        newLieuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nomlieu = editNamneLocation.getText().toString();
                if(!TextUtils.isEmpty(nomlieu)&& postImageUri != null){
                    newLieuBtn.setEnabled(false);
                    //String urlstring = postImageUri.getPath();



                    //store file
                    final String randomName = UUID.randomUUID().toString();
                    File newImageFile = new File(postImageUri.getPath());

                    try {

                        compressedImageFile = new Compressor(NewLocationActivity.this)
                                .setMaxHeight(720)
                                .setMaxWidth(720)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    final Bitmap bitmap = ((BitmapDrawable) imgLocation.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    final StorageReference postStorage = storageReference.child("lieux_voyages").child(randomName+".jpg");
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

                                    compressedImageFile = new Compressor(NewLocationActivity.this)
                                            .setMaxHeight(200)
                                            .setMaxWidth(200)
                                            .setQuality(1)
                                            .compressToBitmap(newThumbFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("lieux_voyages/thumbs")
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
                                        postMap.put("name", nomlieu);
                                        postMap.put("image", downloadUri_post);
                                        postMap.put("latitude",position[0]);
                                        postMap.put("longitude",position[1]);


                                        //todo get voyage
                                        firebaseFirestore.collection("Voyages/"+voyageId+"/lieux").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if(task.isSuccessful()){
                                                    Intent mainIntent = new Intent(NewLocationActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }else {

                                                }
                                            }
                                        });

                                    }
                                });

                            }else {

                            }
                        }
                    });



                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri postImageUrip = data.getData();

                postImageUri = result.getUri();
                imgLocation.setImageURI(postImageUri);



            }  else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }else if(requestCode==1||requestCode==0){

            postImageUri = data.getData();

            imgLocation.setImageURI(postImageUri);
            try {
                exifInterface = new ExifInterface(getContentResolver().openInputStream(postImageUri));
                exifInterface.getLatLong(position);
                Toast.makeText(getApplicationContext(),"post1 "+ position[0]+",post2 "+position[1],Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
