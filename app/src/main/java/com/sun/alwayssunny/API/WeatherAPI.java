package com.sun.alwayssunny.API;

import com.sun.alwayssunny.Classes.WeatherStation;

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

    public static String getWeatherStringFromURL(){
        URL url;
        try {
            url = new URL("http://1-dot-alwayscloudy-1328.appspot.com/alwayssunny");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String jsonString = "";
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            url.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                jsonString += inputLine;

            in.close();
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        return jsonString;
    }

    public static WeatherStation getWeatherStationFromJSONObject(JSONObject json) {
        try {
            String stationName = json.getString("name");
            Double stationLat = json.getDouble("lat");
            Double stationLng = json.getDouble("lon");
            return new WeatherStation(stationName, stationLat, stationLng);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
