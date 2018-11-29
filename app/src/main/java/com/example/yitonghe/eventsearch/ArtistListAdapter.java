package com.example.yitonghe.eventsearch;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder> {

    ArrayList<ArtistItem> artists;

    public ArtistListAdapter(ArrayList<ArtistItem> artists) {
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_item, viewGroup, false);
        ArtistViewHolder artistViewHolder = new ArtistViewHolder(v);
        return artistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder artistViewHolder, int i) {

        artistViewHolder.artistName.setText(artists.get(i).name);

        if(artists.get(i).isMusic) {
            artistViewHolder.artistText.setText(artists.get(i).spotifyName);
            artistViewHolder.followersText.setText(artists.get(i).followers);
            artistViewHolder.popularityText.setText(artists.get(i).popularity);
            artistViewHolder.spotifyText.setText(Html.fromHtml("<a href=\"" + artists.get(i).url + "\">Spotify</a>"));
            artistViewHolder.spotifyText.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            artistViewHolder.artistTable.setVisibility(View.GONE);
        }

        ImageListAdapter imageListAdapter = new ImageListAdapter(artists.get(i).images);
        artistViewHolder.imageList.setAdapter(imageListAdapter);

    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {

        TableLayout artistTable;
        TextView artistName;
        TextView artistText;
        TextView followersText;
        TextView popularityText;
        TextView spotifyText;
        RecyclerView imageList;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistTable = (TableLayout) itemView.findViewById(R.id.artistTable);
            artistName = (TextView) itemView.findViewById(R.id.artistName);
            artistText = (TextView) itemView.findViewById(R.id.artistText);
            followersText = (TextView) itemView.findViewById(R.id.followersText);
            popularityText = (TextView) itemView.findViewById(R.id.popularityText);
            spotifyText = (TextView) itemView.findViewById(R.id.spotifyText);

            imageList = (RecyclerView) itemView.findViewById(R.id.imageList);
            LinearLayoutManager llm = new LinearLayoutManager(itemView.getContext());
            imageList.setLayoutManager(llm);
        }


    }
}
