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
import com.sun.alwayssunny.Service.SunnyService;

import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * Created by Shawn on 4/16/2016.
 */
public class ResultsActivity extends FragmentActivity implements OnMapReadyCallback, ServiceConnection, SunnyService.Callback {

    private SunnyService service;
    protected Address address;
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
        // get a reference to the service, for receiving messages
        Context app = getApplicationContext();
        Intent intent = new Intent(app, SunnyService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        // called when bindService succeeds
        service = ((SunnyService.SunnyServiceBinder) binder).getService();
        service.setListener(this);
        if(map != null){
            onMapReady(map);
        }
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;
        if(service != null && service.foundStations != null){
            onCitiesFound(service.foundStations);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // let's disconnect from the service; it keeps running, though
        if (service != null){
            Context app = getApplicationContext();
            Intent intent = new Intent(app, SunnyService.class);
            stopService(intent);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // called when unbindService succeeds
        if (service != null)
            service.setListener(null);
        service = null;
    }

    @Override
    public void onCitiesFound(ArrayList<WeatherStation> stations) {
        LatLng currentLoc = new LatLng(lat, lng);

        map.setMyLocationEnabled(true);
        map.addMarker(new MarkerOptions()
                .title(service.address.getLocality())
                .position(currentLoc));

        ArrayList<LatLng> stationlatlongs = new ArrayList<LatLng>();
        stationlatlongs.add(currentLoc);
        if (stations.size() >= 3) {
            for (int i = 0; i < 3; i++) {
                WeatherStation station = stations.get(i);
                LatLng stationlatlong = new LatLng(station.latitude, station.longitude);
                map.addMarker(new MarkerOptions()
                        .title(station.stationName)
                        .position(stationlatlong));
                stationlatlongs.add(stationlatlong);
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng stationll : stationlatlongs) {
                builder.include(stationll);
            }
            LatLngBounds bounds = builder.build();
            int padding = 200; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            map.animateCamera(cu);
        }

    }
}
