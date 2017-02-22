package com.domikado.getlocationfused;

import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    TextView textView1, textView2;
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private LocationRequest locationRequest;
    private Double lati, longi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.textview);
        textView2 = (TextView) findViewById(R.id.textview2);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        requestLocationUpdates();

    }

    private void requestLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lati = location.getLatitude();
        longi = location.getLongitude();

        Geocoder geo = new Geocoder(this, Locale.getDefault());

        List<android.location.Address> address;

        try{
            address = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (address.size() > 0){
                String cityname = address.get(0).getLocality().toString();
                textView1.setText(cityname);
            }
        } catch(IOException e){
            e.printStackTrace();
        }

//        textView1.setText(String.valueOf("Lati : "+lati));
        textView2.setText(String.valueOf("Lati : "+lati + " Longi : "+longi));

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()){
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

}
