package com.example.drew.popularmovies;


import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class DetailsActivity extends TabActivity {
    private TextView titleTextView;
    private ImageView imageView;
    private static final String LOG_TAG = GridViewActivity.class.getSimpleName();
    private ArrayAdapter<String> mReviewAdapter;
    private String mMovieID=null;


    private TextView descTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_view);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();


        setContentView(R.layout.activity_details_view);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("About");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Reviews");


        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("About");
        tab1.setContent(R.id.desc);

        tab2.setIndicator("Reviews");
        tab2.setContent(R.id.listview_trailers);



        /** Add the tabs  to the TabHost to display. */
        tabHost.setup();
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);




        final RatingBar ratingBar1 = (RatingBar) findViewById(R.id.ratingbar1);

        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("title");
        String image = bundle.getString("image");
        String desc = bundle.getString("description");
        String year = bundle.getString("year");
        String rating = bundle.getString("rating");
        String titleYear = title + " (" + year + ")";
        float fRating = Float.parseFloat(rating);
        ratingBar1.setRating(fRating / 2);
        mMovieID=bundle.getString("id");

        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(titleYear);
        descTextView = (TextView) findViewById(R.id.desc);
        descTextView.setText(desc);
        descTextView.setMovementMethod(new ScrollingMovementMethod());

        imageView = (ImageView) findViewById(R.id.movie_image);
        Picasso.with(this).load(image).into(imageView);

        //call asynctask for url call with append trailer and reviews

        mReviewAdapter =new ArrayAdapter<String>(
                this,
                R.layout.list_item_trailer,
                R.id.list_item_trailer_textview,
                new ArrayList<String>());


        ListView listView =(ListView)findViewById(R.id.listview_trailers);
        listView.setAdapter(mReviewAdapter);

        //might put onitemclicklistener here
        updateReview();


    }


    private void updateReview(){
        FetchReviewTask reviewTask = new FetchReviewTask();
        reviewTask.execute(mMovieID);
    }


    public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<String >>{

        private ArrayList<String>getReviewDataFromJson(String reviewJsonStr)
        throws JSONException

        {
            final String TMDB_RESULTS="results";
            final String TMDB_REVIEWS = "reviews";
            final String TMDB_AUTHOR= "author";
            final String TMDB_CONTENT="content";

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONObject(TMDB_REVIEWS).getJSONArray(TMDB_RESULTS);

            ArrayList<String> resultStrs=new ArrayList<String>();

            for(int i=0; i<reviewArray.length();i++){
                String author;
                String content;


                JSONObject review =reviewArray.getJSONObject(i);

                author=review.getString(TMDB_AUTHOR);
                content=review.getString(TMDB_CONTENT);

                resultStrs.add(content+"\n"+"-"+author);


            }
            for (String s : resultStrs){
                Log.v(LOG_TAG, "Review entry: "+ s);
            }
            return resultStrs;

        }
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJsonStr = null;

            try

            {
//https://api.themoviedb.org/3/movie/76341?api_key=bb99fbc46e9777b057575f946a19f3f3&append_to_response=trailers,reviews
                String baseURL="https://api.themoviedb.org/3/movie/";
                String movieID=mMovieID;
                String api_key="?api_key=bb99fbc46e9777b057575f946a19f3f3";
                String append="&append_to_response=reviews,videos";
                String fullPath=baseURL+movieID+api_key+append;

                URL url = new URL(fullPath);
                Log.v(LOG_TAG, "Built Uri " + url);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    reviewJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;// I commented this out forecastJsonStr = null;
                }
                reviewJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Reivew JSON String: " + reviewJsonStr);

            } catch (
                    IOException e
                    )

            {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                reviewJsonStr= null;
            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewDataFromJson(reviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
//end copied from Git
        }
        @Override
        protected void onPostExecute(ArrayList<String> result){
            if(result != null){
                mReviewAdapter.clear();
                for(String dayForecastStr : result){
                    mReviewAdapter.add(dayForecastStr);
                }
            }
            if(result.isEmpty()){
                mReviewAdapter.add("No reviews in the movie database yet.");
            }

        }



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);

    }



}