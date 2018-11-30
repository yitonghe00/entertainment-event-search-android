package com.example.yitonghe.eventsearch;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";

    private LinearLayout progressBar;
    private ArrayList<EventItem> results;
    private ListView resultsList;
    private TextView noResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        String path = intent.getStringExtra(SearchTabFragment.EXTRA_MESSAGE);

        // Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.resultsToolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Progress bar
        progressBar = findViewById(R.id.progressBar);
        noResults = (TextView) findViewById(R.id.noResults);

        // Send API call
        ApiCall.make(this, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseObject = new JSONObject(response);
                    results = new ArrayList<>();

                    if(responseObject.has("_embedded")) {
                        JSONObject _embedded = responseObject.getJSONObject("_embedded");

                        if(_embedded.has("events")) {
                            JSONArray events = _embedded.getJSONArray("events");

                            // Extract event items
                            for(int i = 0; i < events.length(); i++) {

                                JSONObject event = events.getJSONObject(i);
                                EventItem result = new EventItem();
                                results.add(result);

                                // Name & id
                                result.name = event.getString("name");
                                result.id = event.getString("id");

                                if (event.has("_embedded")) {

                                    JSONObject _e = event.getJSONObject("_embedded");

                                    // Artists
                                    if (_e.has("attractions")) {
                                        JSONArray artists = _e.getJSONArray("attractions");
                                        for (int j = 0; j < artists.length(); j++) {
                                            result.artists.add(artists.getJSONObject(j).getString("name"));
                                        }
                                    }

                                    // Venues
                                    JSONArray venues = _e.getJSONArray("venues");
                                    if (venues.length() > 0) {
                                        result.venue = venues.getJSONObject(0).getString("name");
                                    }
                                }

                                // Time
                                JSONObject dates = event.getJSONObject("dates").getJSONObject("start");
                                if (dates.has("localDate")) {
                                    result.localDate = dates.getString("localDate");
                                }
                                if (dates.has("localTime")) {
                                    result.localTime = dates.getString("localTime");
                                }

                                // Type
                                if (event.has("classifications")) {
                                    JSONArray classifications = event.getJSONArray("classifications");
                                    if (classifications.length() > 0) {
                                        JSONObject c = classifications.getJSONObject(0);
                                        result.type = c.getJSONObject("segment").getString("name");
                                    }

                                }

                                // Url
                                if (event.has("url")) {
                                    result.url = event.getString("url");
                                }
                            }
                        }
                    }
                    setResultsList();

                } catch (Exception e) {
                    Toast.makeText(ResultsActivity.this, "Failed to get search result", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ResultsActivity.this, "Error when getting search result", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setResultsList();
    }

    private void setResultsList() {

        progressBar.setVisibility(View.GONE);

        if(results.size() == 0) {
            noResults.setVisibility(View.VISIBLE);
            return;
        }

        ArrayAdapter<EventItem> adapter = new ResultsListAdapter(ResultsActivity.this, R.layout.event_item, results);
        resultsList = (ListView) findViewById(R.id.resultsList);
        resultsList.setAdapter(adapter);

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventItem selectedEvent = (EventItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(ResultsActivity.this, DetailsActivity.class);
                intent.putExtra("eventObject", selectedEvent);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
