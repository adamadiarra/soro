package com.soro.diarra.soro;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
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
        context = parent.getContext();
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


        //todo date formate



        Query firstQuery = firebaseFirestore.collection("Voyages/"+voyageId+"/lieux").limit(1);
        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                        String uri = doc.getDocument().getString("image_url");
                        holder.setUriVoyage(uri);
                    }
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("voyage_id",voyageId);
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
                    case R.id.v_supp_m:
                        Toast.makeText(context,"supp voyage",Toast.LENGTH_LONG).show();
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
        private Toolbar vtoolbar;
        private ImageButton imageButton;
        PopupMenu.OnMenuItemClickListener mylistener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            titreView = mView.findViewById(R.id.voyage_titre);
            dateView = mView.findViewById(R.id.date_item);
            voyageView = mView.findViewById(R.id.voyage_img_item);
            imageButton = mView.findViewById(R.id.imageButton);



        }

        public void setVoyadata(String nom,String date){
            titreView.setText(nom);
            dateView.setText(date);

        }

        public void setUriVoyage(String uriVoyage){
            Glide.with(context).load(uriVoyage).into(voyageView);
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
