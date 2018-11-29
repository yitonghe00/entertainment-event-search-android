package com.example.yitonghe.eventsearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteTabFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;
    ArrayList<EventItem> favorites;
    Type listType;

    private ListView favoriteList;
    private TextView noFav;


    public FavoriteTabFragment() {

    }

    public boolean isFavorite(String id) {
        for(EventItem event : this.favorites) {
            if(id.equals(event.id)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<EventItem> getFavorite() {
        return favorites;
    }

    public void addFavorite(EventItem event) {
        favorites.add(event);

        String json = gson.toJson(favorites, listType);
        editor.putString("favorites", json);
        editor.commit();
        setResultsList();
    }

    public void removeFavorite(String id) {
        for(int i = 0; i < favorites.size(); i++) {
            if(id.equals(favorites.get(i).id)) {
                favorites.remove(i);
                break;
            }
        }
        String json = gson.toJson(favorites, listType);
        editor.putString("favorites", json);
        editor.commit();
        setResultsList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_tab_fragment, container,false);

        sharedPreferences = getActivity().getSharedPreferences("favorites", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        listType = new TypeToken<ArrayList<EventItem>>() {}.getType();

        String stored = sharedPreferences.getString("favorites", "");
        if(stored.length() == 0) {
            favorites = new ArrayList<>();
            String json = gson.toJson(favorites, listType);
            editor.putString("favorites", json);
            editor.commit();
        } else {
            favorites = gson.fromJson(stored, listType);
        }

        favoriteList = (ListView) view.findViewById(R.id.favoriteList);
        noFav = (TextView) view.findViewById(R.id.noFav);

        setResultsList();

        return view;
    }

    private void setResultsList() {
        if(favorites.size() == 0) {
            noFav.setVisibility(View.VISIBLE);
            favoriteList.setVisibility(View.GONE);
            return;
        }

        favoriteList.setVisibility(View.VISIBLE);
        noFav.setVisibility(View.GONE);

        ArrayAdapter<EventItem> adapter = new ResultsListAdapter(getContext(), R.layout.event_item, favorites);

        favoriteList.setAdapter(adapter);

        favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventItem selectedEvent = (EventItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("eventObject", selectedEvent);
                startActivity(intent);
            }
        });
    }
}
