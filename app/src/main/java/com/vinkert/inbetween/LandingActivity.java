package com.vinkert.inbetween;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LandingActivity extends AppCompatActivity {
    protected LocationManager locationManager;
    //in meters
    private static final long MINIMUM_DISTANCE_FOR_UPDATE = 1;
    //in milliseconds
    private static final long MINIMUM_TIME_FOR_UPDATE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkPermissions();
        int permissionCheck = ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck == PERMISSION_GRANTED && permissionCheck2 == PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_FOR_UPDATE, MINIMUM_DISTANCE_FOR_UPDATE, new LocListener());
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCurrentLocation();
            }
        });
    }
    int asdf;
    int asdff;
    protected void checkPermissions()   {
        if (ContextCompat.checkSelfPermission(LandingActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LandingActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        asdf);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(LandingActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LandingActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        asdff);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    protected void showCurrentLocation()    {
        int permissionCheck = ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck == PERMISSION_GRANTED && permissionCheck2 == PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation((LocationManager.GPS_PROVIDER));

            if(location != null) {
                String loc = String.format("Longitude: %1$s \n Latitude: %2$s",
                        location.getLongitude(), location.getLatitude());
                Toast.makeText(LandingActivity.this, loc, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LocListener implements LocationListener{
        public void onLocationChanged(Location location)    {
            String loc = String.format("Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude());
            Toast.makeText(LandingActivity.this, loc, Toast.LENGTH_LONG).show();
        }
        public void onStatusChanged(String s, int i, Bundle b){
            Toast.makeText(LandingActivity.this, "Provider status changed", Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s)    {
            Toast.makeText(LandingActivity.this, "Provider disabled by user, GPS is off", Toast.LENGTH_LONG).show();
        }
        public void onProviderEnabled(String s) {
            Toast.makeText(LandingActivity.this, "GPS turned on", Toast.LENGTH_LONG).show();
        }
    }
}
