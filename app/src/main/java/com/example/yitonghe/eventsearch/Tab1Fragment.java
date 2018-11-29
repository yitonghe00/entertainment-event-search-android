package com.example.yitonghe.eventsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    String artists;
    String venue;
    String time;
    String category;
    String priceRange;
    String ticketStatus;
    String url;
    String seatMap;
    boolean done;
    boolean viewCreated;

    TableRow nameRow;
    TextView nameText;
    TableRow venueRow;
    TextView venueText;
    TableRow timeRow;
    TextView timeText;
    TableRow categoryRow;
    TextView categoryText;
    TableRow priceRangeRow;
    TextView priceRangeText;
    TableRow ticketStatusRow;
    TextView ticketStatusText;
    TableRow urlRow;
    TextView urlLink;
    TableRow seatMapRow;
    TextView seatMapLink;
    LinearLayout progressBar;
    TableLayout eventTable;

    public Tab1Fragment() {
        artists = "";
        venue = "";
        time = "";
        category = "";
        priceRange = "";
        ticketStatus = "";
        url = "";
        seatMap = "";
        done = false;
        viewCreated = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        Log.i("DEBUG", "Tab1 onCreateView() run!");

        progressBar = (LinearLayout) view.findViewById(R.id.tab1ProgressBar);
        eventTable = (TableLayout) view.findViewById(R.id.eventTable);

        nameRow =  view.findViewById(R.id.nameRow);
        nameText = view.findViewById(R.id.nameText);

        venueRow =  view.findViewById(R.id.venueRow);
        venueText = view.findViewById(R.id.venueText);

        timeRow =  view.findViewById(R.id.timeRow);
        timeText = view.findViewById(R.id.timeText);

        categoryRow =  view.findViewById(R.id.categoryRow);
        categoryText = view.findViewById(R.id.categoryText);

        priceRangeRow =  view.findViewById(R.id.priceRangeRow);
        priceRangeText = view.findViewById(R.id.priceRangeText);

        ticketStatusRow =  view.findViewById(R.id.ticketStatusRow);
        ticketStatusText = view.findViewById(R.id.ticketStatusText);

        urlRow =  view.findViewById(R.id.urlRow);
        urlLink = view.findViewById(R.id.urlLink);

        seatMapRow =  view.findViewById(R.id.seatMapRow);
        seatMapLink = view.findViewById(R.id.seatMapLink);

        viewCreated = true;

        if(done) {
            display();
        }

        return view;
    }

    private void display() {

        progressBar.setVisibility(View.GONE);
        eventTable.setVisibility(View.VISIBLE);

        if(this.artists.length() == 0) {
            nameRow.setVisibility(View.GONE);
        } else {
            nameText.setText(this.artists);
        }

        if(this.venue.length() == 0) {
            venueRow.setVisibility(View.GONE);
        } else {
            venueText.setText(this.venue);
        }

        if(this.time.length() == 0) {
            timeRow.setVisibility(View.GONE);
        } else {
            timeText.setText(this.time);
        }

        if(this.category.length() == 0) {
            categoryRow.setVisibility(View.GONE);
        } else {
            categoryText.setText(this.category);
        }

        if(this.priceRange.length() == 0) {
            priceRangeRow.setVisibility(View.GONE);
        } else {
            priceRangeText.setText(this.priceRange);
        }

        if(this.ticketStatus.length() == 0) {
            ticketStatusRow.setVisibility(View.GONE);
        } else {
            ticketStatusText.setText(this.ticketStatus);
        }

        if(this.url.length() == 0) {
            urlRow.setVisibility(View.GONE);
        } else {
            urlLink.setText(Html.fromHtml("<a href=\"" + this.url + "\">Ticketmaster</a>"));
            urlLink.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(this.seatMap.length() == 0) {
            seatMapRow.setVisibility(View.GONE);
        } else {
            seatMapLink.setText(Html.fromHtml("<a href=\"" + this.seatMap + "\">View Here</a>"));
            seatMapLink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void onResponse(String response) {

        try {

            JSONObject event = new JSONObject(response);

            if(event.has("_embedded")) {

                JSONObject _e = event.getJSONObject("_embedded");

                // Artists
                if(_e.has("attractions")) {
                    JSONArray artists = _e.getJSONArray("attractions");
                    for(int j = 0; j < artists.length(); j++) {
                        this.artists += artists.getJSONObject(j).getString("name") + " | ";
                    }
                    this.artists = this.artists.substring(0, this.artists.length() - 3);
                }

                // Venues
                JSONArray venues = _e.getJSONArray("venues");
                if(venues.length() > 0) {
                    this.venue = venues.getJSONObject(0).getString("name");
                }
            }

            // Time
            JSONObject dates = event.getJSONObject("dates").getJSONObject("start");
            if(dates.has("localDate")) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date d = simpleDateFormat.parse(dates.getString("localDate"));
                simpleDateFormat.applyPattern("MMM dd, yyyy");
                this.time = simpleDateFormat.format(d);

            }
            if(dates.has("localTime")) {
                this.time += " " + dates.getString("localTime");
            }

            // Category
            if(event.has("classifications")) {
                JSONArray classifications = event.getJSONArray("classifications");

                if(classifications.length() > 0) {
                    JSONObject c = classifications.getJSONObject(0);
                    this.category = c.getJSONObject("segment").getString("name");

                    if(c.has("genre")) {
                        this.category += " | " + c.getJSONObject("genre").getString("name");
                    }
                }
            }

            // Price Range
            if(event.has("priceRanges")) {
                JSONObject priceRange = event.getJSONArray("priceRanges").getJSONObject(0);

                String currency = "$";
                if(!priceRange.getString("currency").equals("USD")) {
                    currency = "(" + priceRange.getString("currency") + ")";
                }

                if(priceRange.has("min") && priceRange.has("max")) {
                    this.priceRange = currency + priceRange.getString("min") + " ~ " + currency + priceRange.getString("max");
                } else {
                    if(priceRange.has("min")) {
                        this.priceRange = currency + priceRange.getString("min");
                    }
                    if(priceRange.has("max")) {
                        this.priceRange = currency + priceRange.getString("max");
                    }
                }
            }

            // Ticket Status
            dates = event.getJSONObject("dates");
            if(dates.has("status")) {
                this.ticketStatus = dates.getJSONObject("status").getString("code");
                this.ticketStatus = ticketStatus.substring(0, 1).toUpperCase() + ticketStatus.substring(1);
            }

            // Buy Ticket At
            if(event.has("url")) {
                this.url = event.getString("url");
            }

            // Seat Map
            if(event.has("seatmap")) {
                this.seatMap = event.getJSONObject("seatmap").getString("staticUrl");
            }

            done = true;

            if(viewCreated) {
                display();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to get event info", Toast.LENGTH_SHORT).show();
        }

    }
}
