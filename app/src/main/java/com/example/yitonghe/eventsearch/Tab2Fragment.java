package com.example.yitonghe.eventsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

public class Tab2Fragment extends Fragment {

    ArrayList<String> artistsData;
    boolean music;

    ArrayList<ArtistItem> artists;
    boolean done;
    int count;
    boolean created;

    RecyclerView artistsList;
    LinearLayout progressBar;
    TextView noRecord;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        artistsList = (RecyclerView) view.findViewById(R.id.artistsList);
        progressBar = (LinearLayout) view.findViewById(R.id.tab2ProgressBar);
        noRecord = (TextView) view.findViewById(R.id.noRecord);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        artistsList.setLayoutManager(llm);

        created = true;
        if(done) {
            display();
        }

        return view;
    }

    public void setArtists(ArrayList<String> artists, boolean isMusic) {
        this.artistsData = artists;
        this.music = isMusic;

        this.artists = new ArrayList<>();
        if(artistsData.size() == 0) {
            return;
        }

        for(int i = 0; i < 2 && i < artistsData.size(); i++) {
            ArtistItem artist = new ArtistItem();
            artist.name = artistsData.get(i);
            setArtist(artist, i);
            this.artists.add(artist);
            count--;
        }
    }

    private void display() {
        progressBar.setVisibility(View.GONE);

        if(artists.size() == 0) {
            noRecord.setVisibility(View.VISIBLE);
            return;
        }
        ArtistListAdapter artistListAdapter = new ArtistListAdapter(artists);
        artistsList.setAdapter(artistListAdapter);
    }

    private void setArtist(final ArtistItem artist, final int i) {
        ApiCall.make(getContext(), "image?keyword=" + artistsData.get(i), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                artist.setImage(response);
                if(music) {
                    count--;
                    ApiCall.make(getContext(), "music?keyword=" + artistsData.get(i), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            artist.setMusic(response);
                            if(++count == 0) {
                                done = true;
                                if(created) {
                                    display();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("ERROR", error.toString());
                            // TODO: Error message
                        }
                    });
                }
                if(++count == 0) {
                    done = true;
                    if(created) {
                        display();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
                // TODO: Error message
            }
        });
    }
}
