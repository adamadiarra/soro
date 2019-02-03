package com.soro.diarra.soro;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private Button saveBtn;
    private EditText UserNameView;
    private EditText UserBirthView;
    private AutoCompleteTextView countryView;
    private FloatingActionButton editBtn;
    private View setupViewer;
    private CircleImageView userImgView;
    private String user_id;
    private BottomNavigationView navigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private Bitmap compressedImageFile;
    private Uri postImageUri = null;
    private Calendar myCalendar;
    private String user_search_id;
    private String friend_state;
    private static final String[] paysData = (new PaysData()).lespays;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = mAuth.getCurrentUser().getUid();
        user_search_id = getActivity().getIntent().getStringExtra("user_id");

        //--------if not from searh-------
        if(user_search_id==null){
            user_search_id = user_id;
        }

        friend_state = "not_friend";

        firebaseFirestore = FirebaseFirestore.getInstance();
        UserNameView = view.findViewById(R.id.user_name_view);
        UserBirthView = view.findViewById(R.id.user_birth_day_view);
        navigationView = view.findViewById(R.id.setup_nav_view);

        editBtn = view.findViewById(R.id.edit_setting_btn);
        saveBtn = view.findViewById(R.id.save_btn);
        setupViewer =  view.findViewById(R.id.contain_viewer);
        userImgView = view.findViewById(R.id.user_im_view);

        // les pays
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, paysData);
        countryView = view.findViewById(R.id.user_country_view);
        countryView.setAdapter(adapter);

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
                new DatePickerDialog(getActivity(), date, myCalendar
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
                        .start(getContext(),ProfileFragment.this);
            }
        });


        UserBirthView.setFocusable(false);

        // retrieve data
        firebaseFirestore.collection("Users").document(user_search_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                        Glide.with(getActivity()).setDefaultRequestOptions(placeholderRequest).load(doc.getString("image")).into(userImgView);
                        enableForm(false);
                        if(!user_id.equals(user_search_id)){
                            navigationView.setVisibility(View.VISIBLE);
                            editBtn.hide();
                        }else {
                            navigationView.setVisibility(View.INVISIBLE);
                            editBtn.show();
                        }
                    }else {
                        editBtn.hide();
                        enableForm(true);
                    }
                }else {

                }

            }
        });


        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_add_friend:
                        //invitation envoyer
                        sendFriendShip(user_search_id);

                        return true;
                    case R.id.nav_send_msg:
                        sendMessageToFriend(user_search_id);
                        return true;
                    case R.id.nav_plus_option:
                        Toast.makeText(getActivity(),"plus option",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
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

        checkFriendShip(user_search_id);
        // Inflate the layout for this fragment
        return view;
    }

    private void checkFriendShip(String user_search_id) {
        Menu menu = navigationView.getMenu();
        MenuItem item =menu.findItem(R.id.nav_add_friend);

        /*
        item.setIcon(R.drawable.ic_friend_user);
        item.setTitle("Ami(e)");

        item.setIcon(R.drawable.ic_invite_user);
        item.setTitle("Invitation envoyé");

        item.setTitle("Ajouter");
        item.setIcon(R.drawable.ic_add_user);
        */
        firebaseFirestore.collection("Users/"+user_id+"/friends").document(user_search_id).addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    friend_state = documentSnapshot.getString("requete");
                    if(friend_state.equals("send")){
                        item.setIcon(R.drawable.ic_invite_user);
                        item.setTitle("Annuler");
                    }else if(friend_state.equals("receive")){
                        friend_state="receive";
                        item.setIcon(R.drawable.ic_invite_user);
                        item.setTitle("accepter");
                    }else {
                        friend_state="friend";
                        item.setIcon(R.drawable.ic_friend_user);
                        item.setTitle("ami(e)");

                    }
                }else {
                    item.setTitle("Ajouter");
                    item.setIcon(R.drawable.ic_add_user);
                    friend_state="not_friend";
                }
            }
        });

    }

    private void sendFriendShip(String user_search_id) {
        //----------if request --------------
        if(friend_state.equals("not_friend")){
            Map<String,String> mapSend = new HashMap<>();
            mapSend.put("requete","send");

            firebaseFirestore.collection("Users/"+user_id+"/friends").document(user_search_id).set(mapSend).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Map<String,String> mapRec = new HashMap<>();
                        mapRec.put("requete","Receive");
                        firebaseFirestore.collection("Users/"+user_search_id+"/friends").document(user_id).set(mapRec).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    checkFriendShip(user_search_id);
                                }
                            }
                        });
                    }
                }
            });



        }
// supprime l'invitation
        if(friend_state.equals("send")||friend_state.equals("friend")){
            firebaseFirestore.collection("Users/"+user_id+"/friends").document(user_search_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        firebaseFirestore.collection("Users/"+user_search_id+"/friends").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    checkFriendShip(user_search_id);
                                }
                            }
                        });
                    }
                }
            });
        }

        if(friend_state.equals("receive")){

        }


    }

    private void  sendMessageToFriend(String user_search_id){

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
        ProgressDialog savingProgress = new ProgressDialog(getActivity());
        savingProgress.setMessage("Saving...");
        savingProgress.setIndeterminate(true);
        savingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if(!TextUtils.isEmpty(nom)&& postImageUri != null&&!TextUtils.isEmpty(birth)&&!TextUtils.isEmpty(country)){
            enableForm(false);
            savingProgress.show();
            final String randomName = UUID.randomUUID().toString();
            File newImageFile = new File(postImageUri.getPath());

            try {

                compressedImageFile = new Compressor(getActivity())
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

                            compressedImageFile = new Compressor(getActivity())
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
                                                    enableForm(false);
                                                    savingProgress.dismiss();
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
        Intent it = new Intent(getActivity(),SetupActivity.class);
        startActivity(it);
        getActivity().finish();
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
