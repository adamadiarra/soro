package com.soro.diarra.soro;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class VoyageReclycleAdapter extends RecyclerView.Adapter<VoyageReclycleAdapter.ViewHolder> {

    public Context context;
    public List<Voyage> voyages;
    private FirebaseFirestore firebaseFirestore;


    public VoyageReclycleAdapter(List<Voyage> voyages) {
        this.voyages = voyages;
    }

    @NonNull
    @Override
    public VoyageReclycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voyage, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new VoyageReclycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        String titre = voyages.get(position).getTitre();
        String date = voyages.get(position).getDate();
        final String voyageId = voyages.get(position).VoyageId;


        Query firstQuery = firebaseFirestore.collection("Voyages/"+voyageId+"/lieux");
        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshots!=null) {
                    if (!documentSnapshots.isEmpty()) {
                        List<String> uris = new ArrayList<>();
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            String uri = doc.getDocument().getString("image");
                            uris.add(uri);
                        }
                        holder.runChangeImage(uris);
                    } else {
                       
                    }
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LieuxActivity.class);
                intent.putExtra("voyage_id",voyageId);
                intent.putExtra("titre",titre);
                context.startActivity(intent);
            }
        });
        holder.setVoyadata(titre,date.toString());

        holder.mylistener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.v_add_lieu_m:
                        Intent intent = new Intent(context,NewLocationActivity.class);
                        intent.putExtra("voyage_id",voyageId);
                        context.startActivity(intent);

                        return true;

                    case R.id.view_map:

                       Intent intentMapVoyage=new Intent(context,VoyageMapActivity.class);
                       intentMapVoyage.putExtra("voyage_id",voyageId);
                       context.startActivity(intentMapVoyage);

                       return true;
                    case R.id.v_supp_m:
                        Query firstQuery1 = firebaseFirestore.collection("Voyages/"+voyageId+"/lieux");
                        firstQuery1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if(!documentSnapshots.isEmpty()){
                                    for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                                        String uri = doc.getDocument().getString("image");
                                        String lieuId = doc.getDocument().getId();
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                firebaseFirestore.collection("Voyages/"+voyageId+"/lieux").document(lieuId)
                                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                            }
                                        });

                                    }
                                }
                                firebaseFirestore.collection("Voyages").document(voyageId).delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    voyages.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }
                        });





                        return true;

                        default:
                            return false;
                }
            }
        };

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showVoyageMenu();
            }
        });

    }



    @Override
    public int getItemCount() {
        if(voyages!=null) {
            return voyages.size();
        }else {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titreView;
        private TextView dateView;
        private ImageView voyageView;
        private View mView;
        private ImageButton imageButton;
        private ImageButton next_im;
        private ImageButton back_im;
        PopupMenu.OnMenuItemClickListener mylistener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            titreView = mView.findViewById(R.id.lieu_nom);
            dateView = mView.findViewById(R.id.date_item_lieu);
            voyageView = mView.findViewById(R.id.lieu_img_item);
            imageButton = mView.findViewById(R.id.imageButton);

            back_im = mView.findViewById(R.id.v_imgleft_btn);
            next_im = mView.findViewById(R.id.v_imright_btn);


        }

        public void setVoyadata(String nom,String date){
            titreView.setText(nom);
            dateView.setText(date);

        }

        public void runChangeImage(List<String> uris){

            final Animation anim_out = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
            final Animation anim_in  = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);


            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                int i = 0;
                @Override
            public void run() {

                    back_im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    next_im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    anim_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            Glide.with(context).load(uris.get(i)).into(voyageView);
                            anim_in.setAnimationListener(new Animation.AnimationListener() {
                                @Override public void onAnimationStart(Animation animation) {}
                                @Override public void onAnimationRepeat(Animation animation) {}
                                @Override public void onAnimationEnd(Animation animation) {}
                            });
                            voyageView.startAnimation(anim_in);


                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    voyageView.startAnimation(anim_out);

                    i++;
                    if (i > uris.size() - 1) {
                        i = 0;
                    }
                    if (i < 0) {
                        i = uris.size() - 1;
                    }
                    handler.postDelayed(this, 5000);
                }


            };
            handler.postDelayed(runnable, 5000);
        }

        public void showVoyageMenu(){
            PopupMenu popupMenu = new PopupMenu(context,imageButton);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.voyage_menu,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(mylistener);
            popupMenu.show();
        }
    }
}
