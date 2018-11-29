package com.example.yitonghe.eventsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArtistItem {

    String name;
    String spotifyName;
    String followers;
    String popularity;
    String url;
    boolean isMusic;
    ArrayList<String> images;

    public ArtistItem() {

        name = "";
        spotifyName = "";
        followers = "";
        popularity = "";
        url = "";
        isMusic = false;
        images = new ArrayList<>();

    }

    public void setImage(String response) {
        try {

            JSONObject imageObj = new JSONObject(response);
            JSONArray links = imageObj.getJSONArray("links");
            for(int i = 0; i < links.length(); i++) {
                images.add(links.getString(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle error
        }

    }

    public void setMusic(String response) {
        try {

            JSONObject musicObj = new JSONObject(response);

            spotifyName = musicObj.getString("name");
            followers = musicObj.getString("followers");
            popularity = musicObj.getString("popularity");
            url = musicObj.getString("url");

            if(spotifyName.length() > 0) {
                isMusic = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle error
        }
    }
}
