package com.example.yitonghe.eventsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ResultsListAdapter extends ArrayAdapter<EventItem> {
    private int resourceId;

    public ResultsListAdapter(Context context, int textViewResourceId, ArrayList<EventItem> results) {
        super(context, textViewResourceId, results);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EventItem event = getItem(position);
        View view;
        final ViewHolder viewHolder;

        if(convertView == null) {

            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();
            viewHolder.eventName = (TextView) view.findViewById(R.id.eventName);
            viewHolder.typeImage = (ImageView) view.findViewById(R.id.typeImage);
            viewHolder.eventVenue = (TextView) view.findViewById(R.id.eventVenue);
            viewHolder.eventTime = (TextView) view.findViewById(R.id.eventTime);
            viewHolder.heartImage = (ImageView) view.findViewById(R.id.heartImage);
            viewHolder.isFav = MainActivity.favoriteTab.isFavorite(event.id);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.eventName.setText(event.name);
        viewHolder.typeImage.setImageResource(event.getImageId());
        viewHolder.eventVenue.setText(event.venue);
        viewHolder.eventTime.setText(event.getTime());
        if(viewHolder.isFav) {
            viewHolder.heartImage.setImageResource(R.drawable.heart_fill_red);
        } else {
            viewHolder.heartImage.setImageResource(R.drawable.heart_outline_black);
        }
        viewHolder.heartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.isFav) {
                    viewHolder.heartImage.setImageResource(R.drawable.heart_outline_black);
                    viewHolder.isFav = false;
                    MainActivity.favoriteTab.removeFavorite(event.id);
                    Toast.makeText(getContext(), event.name + " was removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    viewHolder.heartImage.setImageResource(R.drawable.heart_fill_red);
                    viewHolder.isFav = true;
                    MainActivity.favoriteTab.addFavorite(event);
                    Toast.makeText(getContext(), event.name + " was added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    class ViewHolder{
        TextView eventName;
        ImageView typeImage;
        TextView eventVenue;
        TextView eventTime;
        ImageView heartImage;
        boolean isFav;
    }
}