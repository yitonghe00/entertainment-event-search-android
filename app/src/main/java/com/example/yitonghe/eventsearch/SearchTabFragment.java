package com.example.yitonghe.eventsearch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchTabFragment extends Fragment {

    private static final String TAG = "SearchTabFragment";

    public static final String EXTRA_MESSAGE = "com.example.yitonghe.MESSAGE";

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 500;
    private Handler handler;
    private AutocompleteAdapter autocompleteAdapter;

    private TextView keywordWarning;
    private EditText keyword;
    private Spinner category;
    private EditText distance;
    private Spinner unit;
    private RadioGroup radioGroup;
    private RadioButton current;
    private RadioButton other;
    private FusedLocationProviderClient mFusedLocationClient;
    private double lat;
    private double lng;
    private boolean locationEnabled;
    private EditText location;
    private TextView locationWarning;
    private Button submitButton;
    private Button clearButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_tab_fragment, container, false);

        final AppCompatAutoCompleteTextView autocompleteTextView = (AppCompatAutoCompleteTextView) view.findViewById(R.id.keyword);

        //********
        // KEYWORD
        //********
        keywordWarning = view.findViewById(R.id.keywordWarning);
        keyword = view.findViewById(R.id.keyword);

        // Setting up the adapter for Autocomplete
        autocompleteAdapter = new AutocompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line);
        autocompleteTextView.setAdapter(autocompleteAdapter);
        autocompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                    }
                });

        autocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autocompleteTextView.getText())) {
                        makeAutocompleteApiCall("autocomplete?keyword=" + autocompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });

        //**********
        // CATEGORY
        //**********
        category = view.findViewById(R.id.category);

        //**********
        // DISTANCE
        //**********
        distance = view.findViewById(R.id.distance);
        unit = view.findViewById(R.id.unit);

        //*********
        // LOCATION
        //*********
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return view;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location l) {
                        // Got last known location. In some rare situations this can be null.
                        if (l != null) {
                            lat = l.getLatitude();
                            lng = l.getLongitude();
                        }
                    }
                });

        locationWarning = view.findViewById(R.id.locationWarning);
        location = view.findViewById(R.id.location);

        location = (EditText) view.findViewById(R.id.location);
        location.setEnabled(false);

        //******
        // FROM
        //******

        radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        current = (RadioButton) view.findViewById(R.id.current);
        other = (RadioButton) view.findViewById(R.id.other);
        locationEnabled = false;

        current.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.current) {
                    location.getText().clear();
                    location.setEnabled(false);
                    locationEnabled = false;
                } else {
                    location.setEnabled(true);
                    location.requestFocus();
                    locationEnabled = true;
                }
            }
        });

        //********
        // Submit
        //********
        submitButton = (Button) view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = "search?";

                // Validation
                boolean valid = true;
                if(keyword.getText().toString().trim().length() == 0) {
                    valid = false;
                    keywordWarning.setVisibility(View.VISIBLE);
                }
                if(locationEnabled && location.getText().toString().trim().length() == 0) {
                    valid = false;
                    locationWarning.setVisibility(View.VISIBLE);
                }

                // Submit if valid, else make a Toast
                if(valid) {
                    keywordWarning.setVisibility(View.GONE);
                    locationWarning.setVisibility(View.GONE);

                    path += "keyword=" + keyword.getText().toString();
                    path += "&catagory=" + getResources().getStringArray(R.array.categories_values)[category.getSelectedItemPosition()];
                    if(distance.getText().toString().length() == 0) {
                        path += "&distance=10";
                    } else {
                        path += "&distance=" + (int) Double.parseDouble(distance.getText().toString());
                    }
                    path += "&unit=" + getResources().getStringArray(R.array.units_values)[unit.getSelectedItemPosition()];
                    if(!locationEnabled) {
                        path += "&lat=" + lat + "&lng=" + lng;
                    } else {
                        path += "&location=" +location.getText().toString();
                    }

                    Intent intent = new Intent(getContext(), ResultsActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, path);
                    intent.putExtra(EXTRA_MESSAGE, path);
                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //*******
        // Clear
        //*******
        clearButton = (Button) view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywordWarning.setVisibility(View.GONE);
                locationWarning.setVisibility(View.GONE);

                keyword.setText("");
                category.setSelection(0);
                distance.setText("");
                unit.setSelection(0);
                current.setChecked(true);
                location.getText().clear();
                location.setEnabled(false);
                locationEnabled = false;

                View current = getActivity().getWindow().getCurrentFocus();
                if (current != null) current.clearFocus();
            }
        });


        return view;
    }

    private void makeAutocompleteApiCall(String text) {
        ApiCall.make(getContext(), text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if(responseObject.has("_embedded")) {
                        JSONObject _embedded = responseObject.getJSONObject("_embedded");
                        if(_embedded.has("attractions")) {
                            JSONArray attractions = _embedded.getJSONArray("attractions");
                            for (int i = 0; i < attractions.length(); i++) {
                                JSONObject item = attractions.getJSONObject(i);
                                stringList.add(item.getString("name"));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autocompleteAdapter.setData(stringList);
                autocompleteAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
            }
        });
    }
}
