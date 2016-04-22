package com.sun.alwayssunny.Activity;

import android.app.Activity;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.location.Address;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sun.alwayssunny.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by Shawn on 4/16/2016.
 */
public class ResultsActivity extends FragmentActivity implements OnMapReadyCallback {

    protected Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_view);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);

        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0){
            address = addresses.get(0);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng currentLoc = new LatLng(address.getLatitude(), address.getLongitude());

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));

        map.addMarker(new MarkerOptions()
                .title(address.getLocality())
                .position(currentLoc));
    }

}
