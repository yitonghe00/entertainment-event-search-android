package com.example.yitonghe.eventsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class Tab3Fragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Tab3Fragment";

    boolean done;
    boolean viewCreated;
    String venueName;
    String venueAdd;
    String venueCity;
    String venuePhoneNumber;
    String venueOpenHours;
    String venueGeneralRule;
    String venueChildRule;
    double venueLat;
    double venueLng;

    TableRow venueNameRow;
    TextView venueNameText;
    TableRow venueAddRow;
    TextView venueAddText;
    TableRow venueCityRow;
    TextView venueCityText;
    TableRow venuePhoneNumberRow;
    TextView venuePhoneNumberText;
    TableRow venueOpenHoursRow;
    TextView venueOpenHoursText;
    TableRow venueGeneralRuleRow;
    TextView venueGeneralRuleText;
    TableRow venueChildRuleRow;
    TextView venueChildRuleText;
    MapView mapView;

    LinearLayout progressBar;
    ScrollView venueTable;

    public Tab3Fragment() {
        done = false;
        viewCreated = false;
        venueName = "";
        venueAdd = "";
        venuePhoneNumber = "";
        venueOpenHours = "";
        venueGeneralRule = "";
        venueChildRule = "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment, container,false);

        Log.i("DEBUG", "Tab3 onCreateView() run!");

        venueNameRow = view.findViewById(R.id.venueNameRow);
        venueNameText = view.findViewById(R.id.venueNameText);

        venueAddRow = view.findViewById(R.id.venueAddRow);
        venueAddText = view.findViewById(R.id.venueAddText);

        venueCityRow = view.findViewById(R.id.venueCityRow);
        venueCityText = view.findViewById(R.id.venueCityText);

        venuePhoneNumberRow = view.findViewById(R.id.venuePhoneNumberRow);
        venuePhoneNumberText = view.findViewById(R.id.venuePhoneNumberText);

        venueOpenHoursRow = view.findViewById(R.id.venueOpenHoursRow);
        venueOpenHoursText = view.findViewById(R.id.venueOpenHoursText);

        venueGeneralRuleRow = view.findViewById(R.id.venueGeneralRuleRow);
        venueGeneralRuleText = view.findViewById(R.id.venueGeneralRuleText);

        venueChildRuleRow = view.findViewById(R.id.venueChildRuleRow);
        venueChildRuleText = view.findViewById(R.id.venueChildRuleText);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        progressBar = (LinearLayout) view.findViewById(R.id.tab3ProgressBar);
        venueTable = (ScrollView) view.findViewById(R.id.venueTable);
        viewCreated = true;

        if(done) {
            display();
        }

        return view;
    }

    public void display() {

        progressBar.setVisibility(View.GONE);
        venueTable.setVisibility(View.VISIBLE);

        if(venueName.length() > 0) {
            venueNameText.setText(venueName);
        } else {
            venueNameRow.setVisibility(View.GONE);
        }

        if(venueAdd.length() > 0) {
            venueAddText.setText(venueAdd);
        } else {
            venueAddRow.setVisibility(View.GONE);
        }

        if(venueCity.length() > 0) {
            venueCityText.setText(venueCity);
        } else {
            venueCityRow.setVisibility(View.GONE);
        }

        if(venuePhoneNumber.length() > 0) {
            venuePhoneNumberText.setText(venuePhoneNumber);
        } else {
            venuePhoneNumberRow.setVisibility(View.GONE);
        }

        if(venueOpenHours.length() > 0) {
            venueOpenHoursText.setText(venueOpenHours);
        } else {
            venueOpenHoursRow.setVisibility(View.GONE);
        }

        if(venueGeneralRule.length() > 0) {
            venueGeneralRuleText.setText(venueGeneralRule);
        } else {
            venueGeneralRuleRow.setVisibility(View.GONE);
        }

        if(venueChildRule.length() > 0) {
            venueChildRuleText.setText(venueChildRule);
        } else {
            venueChildRuleRow.setVisibility(View.GONE);
        }

        if(mapView != null) {
            mapView.getMapAsync(this);
        }

    }

    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMinZoomPreference(15);
        LatLng venuePos = new LatLng(venueLat, venueLng);
        googleMap.addMarker(new MarkerOptions().position(venuePos));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(venuePos));
    }

    public void onResponse(String response) {

        try {

            JSONObject venue = new JSONObject(response);

            // Name
            if(venue.has("name")) {
                venueName = venue.getString("name");
            }

            // Address
            if(venue.has("address")) {
                venueAdd = venue.getString("address");
                venueLat = venue.getDouble("lat");
                venueLng = venue.getDouble("lng");
            }

            // City
            if(venue.has("city")) {
                venueCity = venue.getString("city");
            }

            // Phone Number
            if(venue.has("phoneNumber")) {
                venuePhoneNumber = venue.getString("phoneNumber");
            }

            // Open Hours
            if(venue.has("openHours")) {
                venueOpenHours = venue.getString("openHours");
            }

            // General Rule
            if(venue.has("generalRule")) {
                venueGeneralRule = venue.getString("generalRule");
            }

            // Child Rule
            if(venue.has("childRule")) {
                venueChildRule = venue.getString("childRule");
            }

            done = true;

            if(viewCreated) {
                display();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Error handle
        }


    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
