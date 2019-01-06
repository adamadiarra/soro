package com.soro.diarra.soro;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LieuRecycleAdapter extends RecyclerView.Adapter<LieuRecycleAdapter.ViewHolder>  {
    public Context context;
    public List<Lieu> lieux;
    public String voyageId;
    FirebaseFirestore firebaseFirestore;

    public LieuRecycleAdapter(List<Lieu> lieux,String voyageId){
        this.lieux = lieux;
        this.voyageId=voyageId;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new LieuRecycleAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String nom = lieux.get(position).getNom();
        final String uri = lieux.get(position).getImage();
        final float lat = lieux.get(position).getLatitude();
        final float lng = lieux.get(position).getLonitude();
        final String lieuId = lieux.get(position).getId();
        final String datelieu = lieux.get(position).getDate_time();

        holder.setDate(datelieu);
        holder.setLieuData(nom,uri);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context,MapsActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("nom",nom);
                intent.putExtra("uri",uri);
                intent.putExtra("voyage_id",voyageId);
                context.startActivity(intent);
            }
        });

        holder.mylistener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.supp_lieu:
                        firebaseFirestore.collection("Voyages/"+voyageId+"/lieux").document(lieuId).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        lieux.remove(position);
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                        return true;
                    case R.id.edit_lieu:
                        Intent intent = new Intent(context,EditLieuActivity.class);
                        intent.putExtra("lat",lat);
                        intent.putExtra("lng",lng);
                        intent.putExtra("nom",nom);
                        intent.putExtra("uri",uri);
                        intent.putExtra("lieu_id",lieuId);
                        intent.putExtra("voyage_id",voyageId);
                        context.startActivity(intent);

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
        if(lieux!=null){
            return lieux.size();
        }else {
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView nomLieuView;
        private ImageView lieuImageView;
        private TextView  dateView;
        PopupMenu.OnMenuItemClickListener mylistener;
        private ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            nomLieuView = mView.findViewById(R.id.lieu_nom);
            lieuImageView = mView.findViewById(R.id.lieu_img_item);
            imageButton = mView.findViewById(R.id.imageLieuButton);
            dateView = mView.findViewById(R.id.loc_datetime_id);
        }

        public void setLieuData(String nom, String uri){
            nomLieuView.setText(nom);
            Glide.with(context).load(uri).into(lieuImageView);
        }

        public void setDate(String date) {
            dateView.setText(date);
        }

        public void showVoyageMenu(){
            PopupMenu popupMenu = new PopupMenu(context,imageButton);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.lieu_menu,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(mylistener);
            popupMenu.show();
        }
    }
}
