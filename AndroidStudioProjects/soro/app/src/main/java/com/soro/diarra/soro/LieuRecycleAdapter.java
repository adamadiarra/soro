package com.soro.diarra.soro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LieuRecycleAdapter extends RecyclerView.Adapter<LieuRecycleAdapter.ViewHolder>  {
    public Context context;
    public List<Lieu> lieux;
    FirebaseFirestore firebaseFirestore;

    public LieuRecycleAdapter(List<Lieu> lieux){
        this.lieux = lieux;
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
        String nom = lieux.get(position).getNom();
        String uri = lieux.get(position).getImage();
        final float lat = lieux.get(position).getLatitude();
        final float lng = lieux.get(position).getLonitude();

        holder.setLieuData(nom,uri);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"position("+lat+","+lng+")",Toast.LENGTH_LONG).show();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            nomLieuView = mView.findViewById(R.id.lieu_nom);
            lieuImageView = mView.findViewById(R.id.lieu_img_item);
        }

        public void setLieuData(String nom, String uri){
            nomLieuView.setText(nom);
            Glide.with(context).load(uri).into(lieuImageView);
        }
    }
}
