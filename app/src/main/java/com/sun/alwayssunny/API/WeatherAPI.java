package com.sun.alwayssunny.API;

import com.sun.alwayssunny.Classes.WeatherStation;
import com.sun.alwayssunny.Service.HttpGet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Shawn on 4/8/2016.
 */
public class WeatherAPI {

    public static String getWeatherStringFromURL(Double lat, Double lng) throws Exception {
        URL url;
        HttpGet getter = new HttpGet("http://1-dot-alwayscloudy-1328.appspot.com/alwayssunny", "UTF-16");
        getter.addFormFieldSecure("lat", lat.toString());
        getter.addFormFieldSecure("lng", lng.toString());

        String jsonString = null;
        try {
            jsonString = getter.finish();
        } catch (Exception e) {
            throw new Exception(e);
        }

        return jsonString;
    }

    public static WeatherStation getWeatherStationFromJSONObject(JSONObject json) {
        try {
            String stationName = json.getString("name");
            Double stationLat = json.getDouble("lat");
            Double stationLng = json.getDouble("lng");
            return new WeatherStation(stationName, stationLat, stationLng);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
