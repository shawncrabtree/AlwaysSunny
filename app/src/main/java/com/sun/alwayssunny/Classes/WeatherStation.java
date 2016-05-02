package com.sun.alwayssunny.Classes;

import android.location.Location;

/**
 * Created by brett on 5/1/2016.
 */
public class WeatherStation {
    public String stationName;
    public Double latitude;
    public Double longitude;

    public WeatherStation(String name, Location lctn) {
        this.stationName = name;
        this.latitude = lctn.getLatitude();
        this.longitude = lctn.getLongitude();
    }

    public WeatherStation(String name, Double lat, Double lng) {
        this.stationName = name;
        this.latitude = lat;
        this.longitude = lng;
    }

    public WeatherStation(String name, String lat, String lng) {
        this.stationName = name;
        this.latitude = Double.parseDouble(lat);
        this.longitude = Double.parseDouble(lng);
    }
}