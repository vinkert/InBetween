package com.vinkert.inbetween;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.location.Geocoder;


import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LandingActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.autoCompleteTextView1) AutoCompleteTextView location1;
    @BindView(R.id.autoCompleteTextView2) AutoCompleteTextView location2;
    @BindView(R.id.query) EditText searchQuery;

    //@BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.button) Button button;
    protected LocationManager locationManager;
    //in meters
    private static final long MINIMUM_DISTANCE_FOR_UPDATE = 1;
    //in milliseconds
    private static final long MINIMUM_TIME_FOR_UPDATE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        Window window = getWindow();
        checkPermissions();
        buttonEffect(findViewById(R.id.button));
        //http://stackoverflow.com/questions/26703211/android-autocomplete-matching-options
        //Currently the matching matches within the entire string, not with just only the beginning of string
        List<String> locations = new ArrayList<String>();
        //Loads in file of all city, state pairings in US
        locations = populateLocations(R.raw.city_state);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locations);
        location1.setAdapter(adapter);
        location1.setThreshold(1);
        location2.setAdapter(adapter);
        location2.setThreshold(1);

        int permissionCheck = ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck == PERMISSION_GRANTED && permissionCheck2 == PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            /*ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            System.out.println(isWiFi);*/
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_FOR_UPDATE, MINIMUM_DISTANCE_FOR_UPDATE, new LocListener());
        }
    }

    private void transitionToOptions(final double lat, final double longit, final String search)  {
        class RetrieveResults extends AsyncTask<Double, Void, Response<SearchResponse>> {
            Response<SearchResponse> response;
            protected Response<SearchResponse> doInBackground(Double... doubles) {
                try

                {

                    String clientID;
                    String clientSecret;
                    YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
                    YelpFusionApi yelpFusionApi = apiFactory.createAPI(""); //Insert API key here
                    Map<String, String> params = new HashMap<>();
                    //Fremont
                    params.put("term", search);
                    //params.put("term", "Indian food");
//                    params.put("latitude", "40.58114");
//                    params.put("longitude", "-111.914184");
                    params.put("latitude", lat + "");
                    params.put("longitude", longit + "");
                    //Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
                    Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
                    Response<SearchResponse> response = call.execute();
                    SearchResponse searchResponse = response.body();
                    int total = searchResponse.getTotal();
                    return response;
                } catch (
                        Exception e
                        )

                {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(Response<SearchResponse> resp)   {
                double lowerBound = 4.0; //Lowest rating possible that will be displayed
                SearchResponse searchResponse = resp.body();
                int total = searchResponse.getTotal();
                searchResponse.getBusinesses();
                ArrayList<Business> businessList = searchResponse.getBusinesses();
                businessList = pruneBusinesses(businessList, lowerBound);
                OptionsActivity.businesses = businessList;
                Intent optionsIntent = new Intent(LandingActivity.this, OptionsActivity.class);

                //ArrayList<String> imageList = new ArrayList<String>(businessList.size());
//                for(Business b: businessList) {
//                    System.out.println(b.getName() + " " + b.getRating() + "\n" + b.getUrl());
//                    imageList.add(b.getImageUrl());
//                    //ArrayList<String> temp = b.getLocation().getDisplayAddress();
//                    //for(String str: temp)
//                        //System.out.println(str);
//                }
                //optionsIntent.putExtra("businesses", businessList);
                LandingActivity.this.startActivity(optionsIntent);
            }
        }
        new RetrieveResults().execute(lat, longit);
    }

    private ArrayList<Business> pruneBusinesses(ArrayList<Business> businessList, double lowerBound)   {
        ArrayList<Business> deleteList = new ArrayList<Business>();
        for(Business b : businessList) {
            if (b.getRating() < lowerBound)  {
                deleteList.add(b);
            }
        }
        for(Business b : deleteList)    {
            businessList.remove(b);
        }
        return businessList;
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


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LandingActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        asdf);

            }
        }

        if (ContextCompat.checkSelfPermission(LandingActivity.this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LandingActivity.this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        asdf);

            }
        }

        if (ContextCompat.checkSelfPermission(LandingActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this,
                    Manifest.permission.INTERNET)) {


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LandingActivity.this,
                        new String[]{Manifest.permission.INTERNET},
                        asdf);

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
        int permissionCheck3 = ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
        int permissionCheck4 = ContextCompat.checkSelfPermission(LandingActivity.this, Manifest.permission.INTERNET);
        if(permissionCheck == PERMISSION_GRANTED && permissionCheck2 == PERMISSION_GRANTED && permissionCheck3 == PERMISSION_GRANTED
                && permissionCheck4 == PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation((LocationManager.GPS_PROVIDER));

            if(location != null) {
                String loc = String.format("Longitude: %1$s \n Latitude: %2$s",
                        location.getLongitude(), location.getLatitude());
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
            //Toast.makeText(LandingActivity.this, loc, Toast.LENGTH_LONG).show();
        }
        public void onStatusChanged(String s, int i, Bundle b){
            //Toast.makeText(LandingActivity.this, "Provider status changed", Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s)    {
            Toast.makeText(LandingActivity.this, "Provider disabled by user, GPS is off", Toast.LENGTH_LONG).show();
        }
        public void onProviderEnabled(String s) {
            Toast.makeText(LandingActivity.this, "GPS turned on", Toast.LENGTH_LONG).show();
        }
    }

private List<String>  populateLocations(int resourceID){
    List<String> result = new ArrayList<String>();
    InputStream is = this.getResources().openRawResource(resourceID);
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    String readLine = null;

    try {
        // While the BufferedReader readLine is not null
        while ((readLine = br.readLine()) != null) {
            result.add(readLine);
        }
        // Close the InputStream and BufferedReader
        is.close();
        br.close();

    } catch (IOException e) {
        e.printStackTrace();
    }

    return result;
}

public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }


@OnClick(R.id.button)
public void toastLocation(){
    String firstEntry = location1.getText().toString();
    String secondEntry = location2.getText().toString();
    String search = searchQuery.getText().toString();
    if(firstEntry.isEmpty() || secondEntry.isEmpty() || search.isEmpty())   {
        Toast.makeText(LandingActivity.this, "Please make sure both locations are filled in.", Toast.LENGTH_LONG).show();
    }
    else {
        MiddleLocation location1 = ConvertCitytoLatLong(firstEntry);
        MiddleLocation location2 = ConvertCitytoLatLong(secondEntry);
        MiddleLocation loc1 = MiddleLocation.findMiddleLoc(location1, location2);
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        //TODO add checking on the two inputs to make sure the locations are valid

        try {
            //System.out.println(loc1.latitude + "   " + loc1.longitude);
            address = coder.getFromLocation(loc1.latitude,loc1.longitude,5);
            transitionToOptions(loc1.latitude, loc1.longitude, search);
        }catch (Exception e)    {
            e.printStackTrace();
        }
    }
}

private MiddleLocation ConvertCitytoLatLong(String city)    {
    //Pass in via Cityname, statename
    Geocoder coder = new Geocoder(this);
    List<Address> address;
    try {
        address = coder.getFromLocationName(city, 5);
        if(address == null)
            return null;
        MiddleLocation m = new MiddleLocation(address.get(0).getLatitude(), address.get(0).getLongitude());
        //System.out.println(address.get(0).getLatitude() + "   " + address.get(0).getLongitude() + "    " + city);
        return m;
    } catch (Exception e)   {
        e.printStackTrace();
    }
    return null;
}
}
