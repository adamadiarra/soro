package com.soro.diarra.soro;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class GallryRecycleView extends RecyclerView.Adapter<GallryRecycleView.ViewHolder> {
    List<Lieu> lieux;
    Context context;

    public GallryRecycleView(List<Lieu> lieux){
        this.lieux=lieux;
    }
    @NonNull
    @Override
    public GallryRecycleView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        context = parent.getContext();

        return new GallryRecycleView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String nom = lieux.get(position).getNom();
        final String uri = lieux.get(position).getImage();
        final float lat = lieux.get(position).getLatitude();
        final float lng = lieux.get(position).getLonitude();
        final String lieuId = lieux.get(position).getId();
        final String voyageId = lieux.get(position).getVoyageId();

        holder.setLieuImage(uri);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context,LieuxActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("nom",nom);
                intent.putExtra("uri",uri);
                intent.putExtra("voyage_id",voyageId);
                context.startActivity(intent);
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
        private ImageView lieuImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            lieuImageView = mView.findViewById(R.id.gallery_im_item);
        }

        public void setLieuImage(String uri) {
            Glide.with(context).load(uri).into(lieuImageView);
        }
    }


}
