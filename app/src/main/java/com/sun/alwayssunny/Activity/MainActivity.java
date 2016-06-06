package com.sun.alwayssunny.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sun.alwayssunny.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sun.alwayssunny.API.WeatherAPI;
import com.sun.alwayssunny.Classes.WeatherStation;
import com.sun.alwayssunny.Service.DB_LocationHelper;
import com.sun.alwayssunny.Service.SunnyService;

import static com.sun.alwayssunny.R.id.locInfo;

public class MainActivity extends Activity implements LocationListener, ServiceConnection, SunnyService.Callback {

    private SunnyService service;
    protected LocationManager locationManager;
    protected Handler splashScreenHandler;
    protected Runnable goToLocationList;
    protected boolean isLocationsView;
    double lat;
    double lng;
    public String city, state, country;

    private List<WeatherStation> prevLoc = new ArrayList<>();
    public DB_LocationHelper dbHelper;
    public WeatherStation stations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLocationsView = false;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }

        splashScreenHandler = new Handler();
        goToLocationList = new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.locations_view);
                populatePrevLocations();
                populateListView();
                isLocationsView = true;
                foundLocation();
            }
        };
        splashScreenHandler.postDelayed(goToLocationList, 1500);

        // create service that will eventually calculate sunny cities
        Context app = getApplicationContext();
        Intent intent = new Intent(app, SunnyService.class);
        app.startService(intent);

        bindService(intent, this, Context.BIND_AUTO_CREATE);

        // Need to create another context object
        // That way we don't cause leakage problems
        Context app1 = getApplicationContext();
        dbHelper = new DB_LocationHelper(app1);

    }

//*****************************************************************
//** Below needed for ListView stuff **
//*****************************************************************
    private void populatePrevLocations() {
        prevLoc = dbHelper.selectLatestLocations(10);
    }

    private void populateListView() {
        ArrayAdapter<WeatherStation> locAdapter = new PrevLocAdapter();
        ListView list = (ListView)findViewById(R.id.prevListView);
        list.setAdapter(locAdapter);
    }

    private class PrevLocAdapter extends ArrayAdapter<WeatherStation> {
        public PrevLocAdapter() {
            super(MainActivity.this, R.layout.locations_view, prevLoc);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.location_item, parent, false);
            }

            final WeatherStation currPrevLoc = prevLoc.get(position);


//            Button bt = (Button) findViewById(locInfo);
//            bt.setText(state + ", " + country);
//            bt.setEnabled(true);

            TextView PrevLocText = (TextView)itemView.findViewById(locInfo);
            PrevLocText.setText(currPrevLoc.getStationName());

            itemView.setBackgroundResource(R.drawable.textviewbtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoToLocation(currPrevLoc.getLat(), currPrevLoc.getLng());
                }
            });

            return itemView;
        }
    }
//*****************************************************************

    @Override
    protected void onStart() { super.onStart(); }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        // called when bindService succeeds
        service = ((SunnyService.SunnyServiceBinder) binder).getService();
        service.setListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        service.FindSunnyCities(lat, lng);
        Geocoder gcd1 = new Geocoder(getApplicationContext(), Locale.getDefault());

        List<Address> addresses;
        try{
            addresses = gcd1.getFromLocation(lat, lng, 1);
            if(addresses.size() > 0){
                //city = addresses.get(0).getAddressLine(0);
                state = addresses.get(0).getAddressLine(1);
                country = addresses.get(0).getAddressLine(2);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }


        // insertLocation(string city, double lat, double lng)
        // Insert the latest city into our list
        dbHelper.insertLocation(state, lat, lng);

        foundLocation();
    }

    public void foundLocation(){
        if(isLocationsView && state != null && country != null){
            Button bt = (Button) findViewById(R.id.currentLocation);
            //bt.setText(lat + " " + lng + "\n" + city + ", " + state + ", " + country);
            bt.setText(state + ", " + country);
            bt.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // let's disconnect from the service; it keeps running, though
        if (service != null)
            unbindService(this);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // called when unbindService succeeds
        if (service != null)
            service.setListener(null);
        service = null;
    }

    @Override
    public void onCitiesFound(ArrayList<WeatherStation> stations) {

    }

    public void GoToCurrentLocation(View v){
        GoToLocation(lat, lng);
    }

    public void GoToLocation(double gotolat, double gotolng){
        final Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("lat", gotolat);
        intent.putExtra("lng", gotolng);
        this.startActivity(intent);
        splashScreenHandler.removeCallbacks(goToLocationList);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

}