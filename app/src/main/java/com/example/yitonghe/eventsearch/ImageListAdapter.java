package com.example.yitonghe.eventsearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

    ArrayList<String> images;

    public ImageListAdapter(ArrayList<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item, viewGroup, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
        String url = images.get(i);

        Picasso.with(imageViewHolder.context).load(url).into(imageViewHolder.imageItem);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageItem;
        Context context;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = (ImageView) itemView.findViewById(R.id.imageItem);
            context = itemView.getContext();
        }

    }

}
