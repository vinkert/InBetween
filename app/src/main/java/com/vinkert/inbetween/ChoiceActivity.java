package com.vinkert.inbetween;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
//Pretty much loading screen, add a loading symbol or something later.
public class ChoiceActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        Intent intent = getIntent();
        final Double lat = intent.getDoubleExtra("lat",0);
        final Double longit = intent.getDoubleExtra("longit",0);
        final String search = intent.getStringExtra("search");
        class RetrieveResults extends AsyncTask<Double, Void, Response<SearchResponse>> {
            Response<SearchResponse> response;
            protected Response<SearchResponse> doInBackground(Double... doubles) {
                try

                {
                    YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
                    YelpFusionApi yelpFusionApi = apiFactory.createAPI(); //Insert API key here
                    Map<String, String> params = new HashMap<>();
                    //Fremont
                    params.put("term", search);
                    //params.put("term", "Indian food");
//                    params.put("latitude", "40.58114");
//                    params.put("longitude", "-111.914184");
                    params.put("latitude", lat.toString());
                    params.put("longitude", longit.toString());
                    //Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
                    Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
                    Response<SearchResponse> response = call.execute();
                    SearchResponse searchResponse = response.body();
                    int total = searchResponse.getTotal();
                    System.out.println(" asdfasdfasdf " + total);

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
                Intent optionsIntent = new Intent(ChoiceActivity.this, OptionsActivity.class);

                //ArrayList<String> imageList = new ArrayList<String>(businessList.size());
//                for(Business b: businessList) {
//                    System.out.println(b.getName() + " " + b.getRating() + "\n" + b.getUrl());
//                    imageList.add(b.getImageUrl());
//                    //ArrayList<String> temp = b.getLocation().getDisplayAddress();
//                    //for(String str: temp)
//                        //System.out.println(str);
//                }
                //optionsIntent.putExtra("businesses", businessList);
                ChoiceActivity.this.startActivity(optionsIntent);
            }
        }
        new RetrieveResults().execute(lat, longit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    //Look back at this later, seems inefficient
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choice, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_choice, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
