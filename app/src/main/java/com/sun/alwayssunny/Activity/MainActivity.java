package com.sun.alwayssunny.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.sun.alwayssunny.R;

public class MainActivity extends FragmentActivity implements LocationListener {

    protected LocationManager locationManager;
    protected Handler splashScreenHandler;
    protected Runnable goToLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume(){
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        splashScreenHandler = new Handler();
        goToLocationList = new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.locations_view);
                locationManager.removeUpdates(MainActivity.this);
            }
        };
        splashScreenHandler.postDelayed(goToLocationList, 30000);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        locationManager.removeUpdates(this);

        final Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        this.startActivity(intent);
        this.finish();
        splashScreenHandler.removeCallbacks(goToLocationList);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
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
