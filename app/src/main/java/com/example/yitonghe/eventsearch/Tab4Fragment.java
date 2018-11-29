package com.example.yitonghe.eventsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Tab4Fragment extends Fragment {
    private static final String TAG = "Tab4Fragment";

    ArrayList<UpcomingItem> upcomings;
    ArrayList<UpcomingItem> origin;

    ArrayList<String> fields;
    ArrayList<String> options;

    boolean done;
    boolean created;

    RecyclerView upcomingList;
    LinearLayout progressBar;
    LinearLayout upcomingTable;
    TextView noRecord;
    Spinner sortField;
    Spinner sortOption;
    int selectedField;
    int selectedOption;
    Comparator<UpcomingItem> comparator;

    static final int DEFAULT = 0;
    static final int EVENT_NAME = 1;
    static final int TIME = 2;
    static final int ARTIST = 3;
    static final int TYPE = 4;

    static final int ASCENDING = 0;
    static final int DESCENDING = 1;

    public Tab4Fragment() {
        upcomings = new ArrayList<>();
        origin = new ArrayList<>();
        done = false;
        created = false;

        fields = new ArrayList<>();
        fields.add("Default");
        fields.add("Event Name");
        fields.add("Time");
        fields.add("Artist");
        fields.add("Type");

        options = new ArrayList<>();
        options.add("Ascending");
        options.add("Descending");

        selectedField = 0;
        selectedOption = 0;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab4_fragment,container,false);
        upcomingList = (RecyclerView) view.findViewById(R.id.upcomingList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        upcomingList.setLayoutManager(linearLayoutManager);

        sortField = view.findViewById(R.id.sortField);
        sortOption = view.findViewById(R.id.sortOption);

        ArrayAdapter<String> fieldsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, fields);
        sortField.setAdapter(fieldsAdapter);
        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, options);
        sortOption.setAdapter(optionsAdapter);

        sortField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Update upcoming and display
                if(i == 0) {
                    sortOption.setEnabled(false);
                    sortOption.setClickable(false);
                    upcomings.clear();
                    for(UpcomingItem upcoming : origin) {
                        upcomings.add(upcoming);
                    }
                } else {
                    sortOption.setEnabled(true);
                    sortOption.setClickable(true);
                    selectedField = i;
                    sort();
                }

                display();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sortOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Update upcoming and display
                selectedOption = i;

                sort();

                display();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressBar = (LinearLayout) view.findViewById(R.id.tab4ProgressBar);
        upcomingTable = (LinearLayout) view.findViewById(R.id.upcomingTable);
        noRecord = (TextView) view.findViewById(R.id.noRecord);

        created = true;
        if(done) {
            display();
        }

        return view;
    }

    private void sort() {

        comparator = new Comparator<UpcomingItem>() {
            @Override
            public int compare(UpcomingItem o1, UpcomingItem o2) {

                int res = 1;

                switch (selectedField) {

                    case EVENT_NAME:
                        res = o1.name.compareTo(o2.name);
                        break;

                    case TIME:
                        res = o1.date.compareTo(o2.date);
                        if(res == 0) {
                            res = o1.time.compareTo(o2.time);
                        }
                        break;

                    case ARTIST:
                        res = o1.artist.compareTo(o2.artist);
                        break;

                    case TYPE:
                        res = o1.type.compareTo(o2.type);
                        break;

                }

                return selectedOption == 0 ? res : -1 * res;

            }
        };

        Collections.sort(upcomings, comparator);
    }

    private void display() {
        progressBar.setVisibility(View.GONE);
        upcomingTable.setVisibility(View.VISIBLE);

        if(upcomings.size() > 0) {
            upcomingList.setVisibility(View.VISIBLE);
        } else {
            noRecord.setVisibility(View.VISIBLE);
            sortField.setEnabled(false);
            sortField.setClickable(false);
        }

        setResultsList();
    }

    private void setResultsList() {

        UpcomingListAdapter adapter = new UpcomingListAdapter(upcomings);
        upcomingList.setAdapter(adapter);
        adapter.setOnItemClickListener(new UpcomingListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(upcomings.get(position).url));
                startActivity(browserIntent);
            }
        });

    }

    public void onResponse(String response) {
        try {

            JSONArray upcomingArray = new JSONArray(response);
            for(int i = 0; i < 5 && i < upcomingArray.length(); i++) {
                UpcomingItem upcoming = new UpcomingItem();

                upcoming.name = upcomingArray.getJSONObject(i).getString("name");
                upcoming.url = upcomingArray.getJSONObject(i).getString("uri");
                upcoming.artist = upcomingArray.getJSONObject(i).getString("artist");
                upcoming.time = upcomingArray.getJSONObject(i).getString("time");
                upcoming.date = upcomingArray.getJSONObject(i).getString("date");
                upcoming.type = upcomingArray.getJSONObject(i).getString("type");

                upcomings.add(upcoming);
                origin.add(upcoming);
            }

            done = true;

            if(created) {
                display();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to get upcoming event", Toast.LENGTH_SHORT).show();

        }

    }
}
