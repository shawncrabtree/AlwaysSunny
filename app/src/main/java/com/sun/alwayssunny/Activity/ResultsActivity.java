package com.sun.alwayssunny.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.location.Address;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sun.alwayssunny.API.WeatherAPI;
import com.sun.alwayssunny.Classes.WeatherStation;
import com.sun.alwayssunny.R;
import com.sun.alwayssunny.Service.SecurityService;

import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * Created by Shawn on 4/16/2016.
 */
public class ResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    protected GoogleMap map;
    protected double lat;
    protected double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_view);

        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;
        (new GetSunnyCities()).execute(lat, lng);
    }


    public double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    private class GetSunnyCities extends AsyncTask<Double, String, ArrayList<WeatherStation>>
    {
        @Override
        protected ArrayList<WeatherStation> doInBackground(Double... locations) {
            String jsonString = null;
            try {
                jsonString = WeatherAPI.getWeatherStringFromURL(locations[0], locations[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONArray jArray = new JSONArray();
            try {
                jArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<WeatherStation> stations = new ArrayList<WeatherStation>();
            for(int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject jObj = jArray.getJSONObject(i);
                    WeatherStation station = WeatherAPI.getWeatherStationFromJSONObject(jObj);
                    stations.add(station);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return stations;
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherStation> stations) {
            LatLng currentLoc = new LatLng(lat, lng);
            map.setMyLocationEnabled(true);

            ArrayList<LatLng> stationlatlongs = new ArrayList<LatLng>();
            if (stations.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    WeatherStation station = stations.get(i);
                    LatLng stationlatlong = new LatLng(station.latitude, station.longitude);
                    map.addMarker(new MarkerOptions()
                            .title(station.stationName)
                            .position(stationlatlong));
                    stationlatlongs.add(stationlatlong);
                }
                stationlatlongs.add(currentLoc);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng stationll : stationlatlongs) {
                    builder.include(stationll);
                }
                LatLngBounds bounds = builder.build();
                int padding = 200; // offset from edges of the map in pixels
                final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        map.animateCamera(cu);
                    }
                });
            }

        }


    }

}
