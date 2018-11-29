package com.example.yitonghe.eventsearch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class UpcomingListAdapter extends RecyclerView.Adapter<UpcomingListAdapter.UpcomingItemViewHolder> implements View.OnClickListener{

    private ArrayList<UpcomingItem> upcomings;

    private OnItemClickListener mOnItemClickListener = null;

    public UpcomingListAdapter(ArrayList<UpcomingItem> upcomings) {
        this.upcomings = upcomings;
    }

    @Override
    public UpcomingItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upcoming_item, viewGroup, false);
        UpcomingItemViewHolder upcomingItemViewHolder = new UpcomingItemViewHolder(view);
        view.setOnClickListener(this);

        return upcomingItemViewHolder;

    }

    @Override
    public void onBindViewHolder(UpcomingItemViewHolder upcomingItemViewHolder, int i) {
        upcomingItemViewHolder.upcomingName.setText(upcomings.get(i).name);
        upcomingItemViewHolder.upcomingArtist.setText(upcomings.get(i).artist);
        upcomingItemViewHolder.upcomingTime.setText(upcomings.get(i).getTime());
        upcomingItemViewHolder.upcomingType.setText("Type: " + upcomings.get(i).type);
        upcomingItemViewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        return upcomings.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static class UpcomingItemViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView upcomingName;
        TextView upcomingArtist;
        TextView upcomingTime;
        TextView upcomingType;

        UpcomingItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            upcomingName = (TextView) itemView.findViewById(R.id.upcomingName);
            upcomingArtist = (TextView) itemView.findViewById(R.id.upcomingArtist);
            upcomingTime = (TextView) itemView.findViewById(R.id.upcomingTime);
            upcomingType = (TextView) itemView.findViewById(R.id.upcomingType);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
}
