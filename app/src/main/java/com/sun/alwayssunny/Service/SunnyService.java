package com.sun.alwayssunny.Service;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

    public ArrayList<WeatherStation> sunnyStations = null;
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
        void onCitiesFound(ArrayList<WeatherStation> stations);
    }

    private Callback callback;

    public void setListener(Callback callback) {
        this.callback = callback;
    }

    public void FindSunnyCities(){
        new GetSunnyCities().execute();
    }


    private class GetSunnyCities extends AsyncTask<Double, String, ArrayList<WeatherStation>>
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
                    int weatherId = jObj.getJSONArray("weather").getJSONObject(0).getInt("id");
                    if (cloudLevel == 0 && weatherId == 800) {
                        WeatherStation station = WeatherAPI.getWeatherStationFromJSONObject(jObj);
                        stations.add(station);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return stations;
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherStation> stations) {
            SunnyService.this.sunnyStations = stations;
            callback.onCitiesFound(sunnyStations);
        }


    }


}
