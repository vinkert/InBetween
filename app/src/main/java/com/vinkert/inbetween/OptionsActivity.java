package com.vinkert.inbetween;
//Something that was tough to figure out - the onTouchListener (BookmarkListener) was returning true in reaction to gestures, and returning true blocked the gesture from being sent to the next step of the pagaer which let it change pages
//This caused the Viewpager to not be able to change pages smoothly
//TODO: Animate drag and drop of saving locations to bookmarks, via DragEvent
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yelp.fusion.client.models.Business;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.OnClick;

public class OptionsActivity extends AppCompatActivity {

    public static ArrayList<Business> businesses = null;
    private static ArrayList<String> businessImageURL = new ArrayList<String>();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private GestureDetector bookmarkDetector; //For fling down action
    private String currentBusiness;
    private ViewPager mViewPager;
    private HashSet<String> businessSet;
    private int currentPosition;


    View.OnTouchListener touchListener = new View.OnTouchListener()   {
        @Override
        public boolean onTouch(View v, MotionEvent event)   {
            return bookmarkDetector.onTouchEvent(event);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        final int numBusinesses = businesses.size();
        businessSet = new HashSet<>(numBusinesses);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        actionBar.setTitle(1 + " of " + numBusinesses + " locations");
        currentBusiness = businesses.get(0).getName();
        currentPosition = 0;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentBusiness = businesses.get(position).getName();
                currentPosition = position;
                actionBar.setTitle(position + 1 + " of " + numBusinesses + " locations");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bookmarkDetector = new GestureDetector(this, new BookmarkDetector());
        mViewPager.setOnTouchListener(touchListener);



        for(Business b: businesses) {
            //System.out.println(b.getName() + " " + b.getRating() + "\n" + b.getUrl());
            businessImageURL.add(b.getImageUrl());
        }
    }
    class BookmarkDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            if(getSlope(event1.getX(), event1.getY(), event2.getX(),event2.getY()) == 3) {
                if(!businessSet.contains(currentBusiness)) {
                    businessSet.add(currentBusiness);
                    Button button = new Button(getApplicationContext());
                    //ContextThemeWrapper buttonWrapper = new ContextThemeWrapper(getApplicationContext(), R.drawable.button_border)
                    button.setText(currentBusiness);
                    final int businessNumber = currentPosition;
                    button.setTextColor(Color.WHITE);
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            mViewPager.setCurrentItem(businessNumber);
                        }
                    });
                    LinearLayout horizScrollBar = (LinearLayout) findViewById(R.id.scrollbar);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    horizScrollBar.addView(button, lp);
                }

            }
            return false;
        }
    }
    private int getSlope(float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)
            // top
            return 1;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            return 2;
        if (angle < -45 && angle>= -135)
            // down
            return 3;
        if (angle > -45 && angle <= 45)
            // right
            return 4;
        return 0;
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
        if (id == R.id.action_tips) {
            Toast.makeText(OptionsActivity.this, "Try swiping down to save a location to bookmarks!", Toast.LENGTH_LONG).show();
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
            final Business b = businesses.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1);
            textView.setText(b.getName());
//            double dist = b.getDistance()/1609.34;
//            BigDecimal dec = new BigDecimal(dist);
//            dec = dec.setScale(1, RoundingMode.HALF_UP);
//            distTV.setText(dec + " mi");
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
            TextView addressView = (TextView) rootView.findViewById(R.id.address);
            addressView.setText("\n" + b.getLocation().getAddress1() + "\n" + b.getLocation().getCity() + ", " + b.getLocation().getState() + " " + b.getLocation().getZipCode());
            addressView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    String map = "http://maps.google.co.in/maps?q=" + b.getLocation().getAddress1() + b.getLocation().getCity() + ", " + b.getLocation().getState() + " " + b.getLocation().getZipCode();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(intent);
                }
            });
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
