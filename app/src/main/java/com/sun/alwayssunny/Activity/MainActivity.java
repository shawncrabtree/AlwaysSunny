package com.sun.alwayssunny.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sun.alwayssunny.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sun.alwayssunny.API.WeatherAPI;
import com.sun.alwayssunny.Classes.WeatherStation;

public class MainActivity extends Activity implements LocationListener {

    protected LocationManager locationManager;
    protected Handler splashScreenHandler;
    protected Runnable goToLocationList;
    protected boolean isLocationsView;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLocationsView = false;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }

        splashScreenHandler = new Handler();
        goToLocationList = new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.locations_view);
                isLocationsView = true;
            }
        };
        splashScreenHandler.postDelayed(goToLocationList, 2000);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        new getWeatherData().execute(lat, lng);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }

        if(!isLocationsView){
            GoToCurrentLocation();
        }
        else{
            Button bt = (Button) findViewById(R.id.currentLocation);
            bt.setText(lat + " " + lng);
            bt.setEnabled(true);
        }
    }

    public void GoToCurrentLocation(View v){
        GoToCurrentLocation();
    }

    public void GoToCurrentLocation(){
        final Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        this.startActivity(intent);
        this.finish();
        splashScreenHandler.removeCallbacks(goToLocationList);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    private class getWeatherData extends AsyncTask<Double, String, Void>
    {
        @Override
        protected Void doInBackground(Double... locations) {
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
            return null;
        }
    }
}