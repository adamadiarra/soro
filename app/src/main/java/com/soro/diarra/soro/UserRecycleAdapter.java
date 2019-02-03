package com.soro.diarra.soro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecycleAdapter extends RecyclerView.Adapter<UserRecycleAdapter.ViewHolder> implements Filterable {
    Context context;
    List<User> usrLists;
    private List<User> userListFiltered;
    private UserAdapterListener listener;

    public UserRecycleAdapter(Context context,List<User> usrLists,UserAdapterListener listener){
        this.usrLists=usrLists;
        this.context = context;
        this.usrLists = usrLists;
        this.userListFiltered = usrLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
        return new UserRecycleAdapter.ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String nom = userListFiltered.get(i).getName();
        String pays = userListFiltered.get(i).getCountry();
        String image = userListFiltered.get(i).getImage();

        viewHolder.setUserData(nom,pays,image);

    }

    @Override
    public int getItemCount() {
        if(usrLists!=null){
            return userListFiltered.size();
        }else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    userListFiltered = usrLists;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User row : usrLists) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) ) {
                            Toast.makeText(context,"dans adapter : "+charString,Toast.LENGTH_LONG).show();
                            filteredList.add(row);
                        }
                    }

                    userListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userListFiltered = (ArrayList<User>) results.values;
                notifyDataSetChanged();

            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private CircleImageView imageView;
        private TextView nomView;
        private TextView paysView;
        private UserAdapterListener listener;

        public ViewHolder(@NonNull View itemView,UserAdapterListener listener) {
            super(itemView);
            view = itemView;
            this.listener = listener;
            nomView = view.findViewById(R.id.item_user_name);
            paysView = view.findViewById(R.id.item_user_country);
            imageView = view.findViewById(R.id.item_user_image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    if(listener!=null) {
                        listener.onUserSelected(userListFiltered.get(getAdapterPosition()));
                    }
                }
            });


        }

        public void setUserData(String nom,String pays,String image){
            nomView.setText(nom);
            paysView.setText(pays);
            Glide.with(context).load(image).into(imageView);
        }
    }

    interface UserAdapterListener {
        void onUserSelected(User user);
    }
}
