package com.example.yitonghe.eventsearch;

import java.io.Serializable;
import java.util.ArrayList;

public class EventItem implements Serializable {

    String name;
    String id;
    ArrayList<String> artists;
    String venue;
    String localTime;
    String localDate;
    String type;
    String url;

    public EventItem() {
        this.name = "";
        this.id = "";
        this.artists = new ArrayList<>();
        this.venue = "";
        this.localTime = "";
        this.localDate = "";
        this.type = "";
        this.url = "";
    }

    public int getImageId() {
        if(type.equals("Sports")) {
            return R.drawable.sport_icon;
        }
        if(type.equals("Arts & Theatre")) {
            return R.drawable.art_icon;
        }
        if(type.equals("Film")) {
            return R.drawable.film_icon;
        }
        if(type.equals("Miscellaneous")) {
            return R.drawable.miscellaneous_icon;
        }
        return R.drawable.music_icon;
    }

    public String getTime() {
        return this.localDate + " " + this.localTime;
    }
}
