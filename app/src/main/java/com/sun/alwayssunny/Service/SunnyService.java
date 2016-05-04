package com.sun.alwayssunny.Service;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sun.alwayssunny.API.WeatherAPI;
import com.sun.alwayssunny.Classes.WeatherStation;

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
 * Created by Shawn on 5/3/2016.
 */
public class SunnyService extends Service {

    public ArrayList<WeatherStation> foundStations = null;
    public double lat;
    public double lng;
    public Address address;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // returns an object that can return a reference to the service
        return new SunnyServiceBinder();
    }

    public class SunnyServiceBinder extends Binder {
        public SunnyService getService() {
            return SunnyService.this;
        }
    }

    /*** Specify a means for the activity to receive measurements back ***/
    public interface Callback {
        public void onCitiesFound(ArrayList<WeatherStation> stations);
    }

    private Callback callback;

    public void setListener(Callback callback) {
        this.callback = callback;
    }

    public void FindSunnyCities(double lat, double lng){
        new GetSunnyCities().execute(lat, lng);
    }


    private class GetSunnyCities extends AsyncTask<Double, String, ArrayList<WeatherStation>>
    {
        @Override
        protected ArrayList<WeatherStation> doInBackground(Double... locations) {
            lat = locations[0];
            lng = locations[1];

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

            Collections.sort(stations, new Comparator<WeatherStation>() {
                        @Override
                        public int compare(WeatherStation w1, WeatherStation w2) {
                            double w1dist = distance(w1.latitude, w1.longitude, lat, lng);
                            double w2dist = distance(w2.latitude, w2.longitude, lat, lng);
                            return w1dist > w2dist ? 1 : -1;
                        }
                    }
            );
            return stations;
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherStation> stations) {
            SunnyService.this.foundStations = stations;
            callback.onCitiesFound(foundStations);
        }

        public double distance(double lat1, double lng1, double lat2, double lng2) {
            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians(lat2-lat1);
            double dLng = Math.toRadians(lng2-lng1);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLng/2) * Math.sin(dLng/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double dist = earthRadius * c;

            return dist;
        }
    }


}
