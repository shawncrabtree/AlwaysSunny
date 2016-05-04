package com.sun.alwayssunny.API;

import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;

import com.sun.alwayssunny.Activity.MainActivity;
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
            url = new URL("http://api.openweathermap.org/data/2.5/box/city?bbox=-130.57,50.44,-59.19,23.14,100000&cluster=no&units=imperial&appid=2ab91d37d2983284cd0e8a970e078544");
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
            JSONObject coord = json.getJSONObject("coord");
            Double stationLat = coord.getDouble("lat");
            Double stationLng = coord.getDouble("lon");

            WeatherStation station = new WeatherStation(stationName, stationLat, stationLng);
            return station;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
