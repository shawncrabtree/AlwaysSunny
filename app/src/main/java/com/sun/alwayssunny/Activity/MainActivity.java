package com.sun.alwayssunny.Activity;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sun.alwayssunny.API.WeatherAPI;
import com.sun.alwayssunny.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {

    protected LocationManager locationManager;
    protected WeatherAPI weatherService;
    protected TextView txtLat;
    protected TextView cityTV;
    protected TextView weatherTV;
    protected TextView sunnyTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLat = (TextView) findViewById(R.id.locationTV);
        cityTV = (TextView) findViewById(R.id.cityTV);
        weatherTV = (TextView) findViewById(R.id.weatherTV);
        sunnyTV = (TextView) findViewById(R.id.sunnyTV);
    }

    @Override
    public void onResume(){
        super.onResume();
        txtLat.setText(R.string.your_location);
        cityTV.setText(R.string.your_location);
        weatherTV.setText(R.string.your_location);
        sunnyTV.setText(R.string.your_location);
        weatherService = new WeatherAPI(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        txtLat.setText("Latitude:" + lat + ", Longitude:" + lng);
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = null;
        if (addresses != null && addresses.size() > 0){
            address = addresses.get(0);
        }

        cityTV.setText(address.getLocality());
        weatherService.execute(address);
        locationManager.removeUpdates(this);
    }

    public void setWeather(final String weather){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weatherTV.setText(weather);
                sunnyTV.setText(isSunny(weather));
            }
        });
    }

    private String isSunny(String weather){
        return String.valueOf(weather.toLowerCase().contains("sun"));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}
