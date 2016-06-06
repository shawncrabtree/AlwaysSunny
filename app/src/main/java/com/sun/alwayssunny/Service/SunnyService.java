package com.sun.alwayssunny.Service;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sun.alwayssunny.API.WeatherAPI;
import com.sun.alwayssunny.Classes.WeatherStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    public void FindSunnyCities(Double lat, Double lng){
        new GetSunnyCities().execute(lat, lng);
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
            SunnyService.this.sunnyStations = stations;
            callback.onCitiesFound(sunnyStations);
        }


    }


}
