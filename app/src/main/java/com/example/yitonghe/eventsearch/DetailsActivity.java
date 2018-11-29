package com.example.yitonghe.eventsearch;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;


public class DetailsActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    EventItem event;
    ImageView barHeartImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get passed data
        Intent i = getIntent();
        event = (EventItem) i.getSerializableExtra("eventObject");

        // Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.detailsToolbar);
        setSupportActionBar(myToolbar);
        barHeartImage = (ImageView) findViewById(R.id.barHeartImage);
        if(MainActivity.favoriteTab.isFavorite(event.id)) {
            barHeartImage.setImageResource(R.drawable.heart_fill_red);
        } else {
            barHeartImage.setImageResource(R.drawable.heart_fill_white);
        }
        barHeartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.favoriteTab.isFavorite(event.id)) {
                    barHeartImage.setImageResource(R.drawable.heart_fill_white);
                    MainActivity.favoriteTab.removeFavorite(event.id);
                    Toast.makeText(DetailsActivity.this, event.name + " was removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    barHeartImage.setImageResource(R.drawable.heart_fill_red);
                    MainActivity.favoriteTab.addFavorite(event);
                    Toast.makeText(DetailsActivity.this, event.name + " was added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(event.name);
        }

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.info_outline);
        tabLayout.getTabAt(1).setIcon(R.drawable.artist);
        tabLayout.getTabAt(2).setIcon(R.drawable.venue);
        tabLayout.getTabAt(3).setIcon(R.drawable.upcoming);

        sendApiCalls();
    }

    private void sendApiCalls() {

        final SectionsPageAdapter adapter = (SectionsPageAdapter) mViewPager.getAdapter();

        // Detail API
        String path = "detail?id=" + event.id;

        ApiCall.make(this, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((Tab1Fragment) adapter.getItem(0)).onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
                // TODO: Error message
            }
        });

        // Artist
        ((Tab2Fragment) adapter.getItem(1)).setArtists(event.artists, event.type.equals("Music"));


        // Venue API
        path = "venue?keyword=" + event.venue;
        ApiCall.make(this, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((Tab3Fragment) adapter.getItem(2)).onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
                // TODO: Error message
            }
        });

        // Upcoming API
        path = "upcoming?keyword=" + event.venue;
        ApiCall.make(this, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ((Tab4Fragment) adapter.getItem(3)).onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
                // TODO: Error message
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "EVENT");
        adapter.addFragment(new Tab2Fragment(), "ARTIST(S)");
        adapter.addFragment(new Tab3Fragment(), "VENUE");
        adapter.addFragment(new Tab4Fragment(), "UPCOMING");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Setup return arrow
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickTwitterButton(View view) {
        String url = "https://twitter.com/intent/tweet?text=Check out " + event.name + " located at " + event.venue + ". Website: " + event.url + "&hashtags=CSCI571EventSearch";
        Log.i("DetailsActivity", url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
