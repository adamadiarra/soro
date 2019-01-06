package com.soro.diarra.soro;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context context;
    private  List<String> values;

    public ImageAdapter(List<String> values){
        this.values= values;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img_map, parent, false);
        context = parent.getContext();
        return new ImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        String uri = values.get(i);
        viewHolder.setImageView(uri);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context,FullScreenActivity.class);
                it.putExtra("uri",uri);
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (values!=null){
            return  values.size();
        }else {
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = view.findViewById(R.id.im_it_map_v);
        }

        public void setImageView(String uri) {
            Glide.with(context).load(uri).into(imageView);
        }
    }
}
