package com.soro.diarra.soro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

public class VoyageReclycleAdapter extends RecyclerView.Adapter<VoyageReclycleAdapter.ViewHolder> {

    public Context context;
    public List<Voyage> voyages;

    public VoyageReclycleAdapter(List<Voyage> voyages) {
        this.voyages = voyages;
    }

    @NonNull
    @Override
    public VoyageReclycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voyage, parent, false);
        context = parent.getContext();
        return new VoyageReclycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        String titre = voyages.get(position).getTitre();
        Date date = voyages.get(position).getDate();
        String uri = voyages.get(position).getImage();

        //todo date formate


        holder.setVoyadata(titre,uri,date.toString());
    }



    @Override
    public int getItemCount() {
        if(voyages!=null) {
            return voyages.size();
        }else {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titreView;
        private TextView dateView;
        private ImageView uriVoyage;
        private View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            titreView = mView.findViewById(R.id.voyage_titre);
            dateView = mView.findViewById(R.id.date_item);
            uriVoyage = mView.findViewById(R.id.voyage_img_item);

        }

        public void setVoyadata(String nom, String uri,String date){
            titreView.setText(nom);
            dateView.setText(date);
            Glide.with(context.getApplicationContext()).load(uri).into(uriVoyage);

        }

    }
}
