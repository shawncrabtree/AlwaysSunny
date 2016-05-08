package com.sun.alwayssunny.Classes;

import android.location.Location;

import java.util.List;

/**
 * Created by brett on 5/1/2016.
 */
public class WeatherStation {
    public String stationName;
    public Double latitude;
    public Double longitude;

    public WeatherStation() {}

    public WeatherStation(String name, Double lat, Double lng) {
        this.stationName = name;
        this.latitude = lat;
        this.longitude = lng;
    }

    public String getStationName() { return this.stationName; }

    public Double getLat() { return this.latitude; }

    public Double getLng() { return this.longitude; }
}