package com.vinkert.inbetween;

//TODO: drag and drop each location's title to the action bar then save it there as a bookmark (allows users to populate list of possible places)
//TODO: figure out how to make sectionpageadapter not take up full screen to add space for above feature (e.g. swiping left on bottom does not change page)
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.yelp.fusion.client.models.Business;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.OnClick;

public class OptionsActivity extends AppCompatActivity {

    public static ArrayList<Business> businesses = null;
    private static ArrayList<String> businessImageURL = new ArrayList<String>();
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
        setContentView(R.layout.activity_options);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        ActionBar actionBar = getActionBar();
//        actionBar.setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //BusinessWrapper wrap = (BusinessWrapper) getIntent().getSerializableExtra("businesses");
//        ArrayList<Business> businesses = (ArrayList<Business>) getIntent().getSerializableExtra("business");
//        for(Business b: businesses)
//            System.out.println(b.getName() + " " + b.getRating());

        for(Business b: businesses) {
            //System.out.println(b.getName() + " " + b.getRating() + "\n" + b.getUrl());
            businessImageURL.add(b.getImageUrl());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_options, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            TextView distTV = (TextView) rootView.findViewById(R.id.distance);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            final Business b = businesses.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1);
            textView.setText(b.getName());
            double dist = b.getDistance()/1609.34;
            BigDecimal dec = new BigDecimal(dist);
            dec = dec.setScale(1, RoundingMode.HALF_UP);
            distTV.setText(dec + " mi");
            ImageView yelpLogo = (ImageView) rootView.findViewById(R.id.yelp_logo);
            yelpLogo.setImageResource(R.drawable.yelp_logo);
            yelpLogo.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(b.getUrl()));
                    startActivity(intent);
                }
            });
            ImageView imageView = (ImageView) rootView.findViewById(R.id.coverImage);
            setRatingsView((ImageView) rootView.findViewById(R.id.ratingsImage), b);
            //imageView.setImageResource(R.mipmap.ic_launcher);
            //new DownloadImageTask(imageView)
            //        .execute("https://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");
            new DownloadImageTask(imageView)
                    .execute(b.getImageUrl());
            return rootView;
        }
        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    System.out.println(urldisplay);
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
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
            return businesses.size();
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "SECTION 1";
//                case 1:
//                    return "SECTION 2";
//                case 2:
//                    return "SECTION 3";
//            }
//            //return null;
//        }
    }

    private static void setRatingsView (ImageView img, Business b) {
        double rating = b.getRating();
        if(rating == 4.0)
            img.setImageResource(R.drawable.stars_extra_large_4);
        else if(rating == 4.5)
            img.setImageResource(R.drawable.stars_extra_large_4_half);
        else if(rating == 5.0)
            img.setImageResource(R.drawable.stars_extra_large_5);
    }
}
