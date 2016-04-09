package com.sun.alwayssunny.API;

import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;

import com.sun.alwayssunny.Activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Shawn on 4/8/2016.
 */
public class WeatherAPI extends AsyncTask<Address, Integer, String> {

    private final String appid = "&APPID=6da92a2d64daa244d019613434ffe49f";
    private MainActivity activity;

    public WeatherAPI(MainActivity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Address ... addresses) {
        String jsonString = getWeatherJson(addresses[0]);
        String weather = getWeather(jsonString);
        this.activity.setWeather(weather);
        return jsonString;
    }


    // todo use params http://stackoverflow.com/questions/2959316/how-to-add-parameters-to-a-http-get-request-in-android
    public String getWeatherJson(Address address){
        URL url;
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/weather?zip="
                                + address.getPostalCode() + ","
                                + address.getCountryCode()
                                + appid);
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

    public String getWeather(String jsonString){
        String weatherMain = "";
        try{
            JSONObject jObject = new JSONObject(jsonString);
            JSONObject weather = jObject.getJSONArray("weather")
                    .getJSONObject(0);
            weatherMain = weather.getString("main");
            // todo also consider description
            //weather.getString("description");

        }catch (JSONException e){
            throw new RuntimeException(e);
        }

        return weatherMain;
    }

}
