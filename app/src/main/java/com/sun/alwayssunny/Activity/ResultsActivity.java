package com.sun.alwayssunny.Activity;

import android.app.Activity;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.location.Address;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sun.alwayssunny.API.WeatherAPI;
import com.sun.alwayssunny.Classes.WeatherStation;
import com.sun.alwayssunny.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Shawn on 4/16/2016.
 */
public class ResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    protected Address address;
    protected GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_view);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);

        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0){
            address = addresses.get(0);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new getWeatherData().execute(lat, lng);
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;
        LatLng currentLoc = new LatLng(address.getLatitude(), address.getLongitude());

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));

        map.addMarker(new MarkerOptions()
                .title(address.getLocality())
                .position(currentLoc));
    }

    private class getWeatherData extends AsyncTask<Double, String, ArrayList<WeatherStation>>
    {
        @Override
        protected ArrayList<WeatherStation> doInBackground(Double... locations) {
            JSONObject reader;
            JSONArray jArray = new JSONArray();
            String jsonString = WeatherAPI.getWeatherStringFromURL();
            try {
                reader = new JSONObject(jsonString);
                jArray = reader.getJSONArray("list");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<WeatherStation> stations = new ArrayList<WeatherStation>();
            for(int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject jObj = jArray.getJSONObject(i);
                    int cloudLevel = jObj.getJSONObject("clouds").getInt("all");
                    if (cloudLevel == 0) {
                        WeatherStation station = WeatherAPI.getWeatherStationFromJSONObject(jObj);
                        stations.add(station);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // TODO: Do something with the data retrieved by the async task.
            return stations;
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherStation> stations) {
            if(stations.size() >= 3){
                for(int i = 0; i < 3; i++){
                    WeatherStation station = stations.get(i);
                    LatLng stationlatlong = new LatLng(station.latitude, station.longitude);
                    map.addMarker(new MarkerOptions()
                            .position(stationlatlong));
                }
            }
        }
    }

}
