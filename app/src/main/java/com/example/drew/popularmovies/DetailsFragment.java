package com.example.drew.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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


public class DetailsFragment extends MovieFragment {

    private TextView titleTextView;
    private ImageView imageView;
    private static final String LOG_TAG = GridViewActivity.class.getSimpleName();
    private ArrayAdapter<String> mReviewAdapter;
    private ArrayAdapter<String> mTrailerAdapter;
    private String mTitle;
    private String mMovieID;
    private String mImage;
    private String mRating;
    private String mDesc;
    private String mYear;
    private ImageButton mButton;

    private String mSort;

    private TextView descTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_fragment, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_details_view, container, false);











        final RatingBar ratingBar1 = (RatingBar) rootView.findViewById(R.id.ratingbar1);

        Bundle bundle = getActivity().getIntent().getExtras();

        mTitle = bundle.getString("title");
        mImage = bundle.getString("image");
        mDesc = bundle.getString("description");
        mYear = bundle.getString("year");
        mRating = bundle.getString("rating");
        String titleYear = mTitle + " (" + mYear + ")";
        float fRating = Float.parseFloat(mRating);
        ratingBar1.setRating(fRating / 2);
        mMovieID=bundle.getString("id");







        mButton = (ImageButton) rootView.findViewById(R.id.favorite_button);


        mButton.setOnClickListener(new View.OnClickListener() {

            MySQLiteHelper cb = new MySQLiteHelper(getActivity().getApplicationContext());
            boolean mState = cb.getBook(mMovieID);

            public void onClick(View v) {
                if (mState) {
                    //remove movieID from database list that will generate from updateMovie else statement
                    MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
                    db.deleteBook(new Movie(mTitle, mMovieID, mYear, mRating, mDesc, mImage));
                    Log.v(LOG_TAG, "All Favorites in db: " + db.getAllBooks());
                    Log.v(LOG_TAG, "unfav");
                    mState = false;

                    Context context = getActivity().getApplicationContext();
                    CharSequence text = mTitle + " removed from favorites";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);

                    toast.show();

                } else if (!mState) {
                    //add movieID to database list that will generate from updateMovie else statement
                    //Movie movie =new Movie(mTitle,mMovieID);\

                    MySQLiteHelper db = new MySQLiteHelper(getActivity().getApplicationContext());
                    db.addBook(new Movie(mTitle, mMovieID, mYear, mRating, mDesc, mImage));

                    Context context = getActivity().getApplicationContext();
                    CharSequence text = mTitle + " added to favorites";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);

                    toast.show();

                    Log.v(LOG_TAG, "fav");


                    Log.v(LOG_TAG, "All Favorites in db: " + db.getAllBooks());

                    mState = true;

                }


            }
        });




        titleTextView = (TextView)rootView.findViewById(R.id.title);
        titleTextView.setText(titleYear);
        descTextView = (TextView) rootView.findViewById(R.id.desc);
        descTextView.setText(mDesc);
        descTextView.setMovementMethod(new ScrollingMovementMethod());

        imageView = (ImageView) rootView.findViewById(R.id.movie_image);
        Picasso.with(getActivity()).load(mImage).into(imageView);



        //call asynctask for url call with append trailer and reviews

        mReviewAdapter =new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_review,
                R.id.list_item_review_textview,
                new ArrayList<String>());


        ListView listView =(ListView)rootView.findViewById(R.id.listview_reviews);
        listView.setAdapter(mReviewAdapter);

        //might put onitemclicklistener here
        updateReview();


        mTrailerAdapter =new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_trailer,
                R.id.list_item_trailer_textview,
                new ArrayList<String>());

        ListView trailerListView =(ListView)rootView.findViewById(R.id.listview_trailers);
        trailerListView.setAdapter(mTrailerAdapter);

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String trailer = mTrailerAdapter.getItem(position);
                String[] splitStr = trailer.split("\\n+");

                if(splitStr.length>1) {
                    String trailerPath=splitStr[1];
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerPath));
                    startActivity(intent);
                }

            }
        });

        updateTrailers();

        return rootView;

    }




    private void updateReview(){
        FetchReviewTask reviewTask = new FetchReviewTask();
        reviewTask.execute(mMovieID);
    }

    private void updateTrailers(){
        FetchTrailerTask trailerTask = new FetchTrailerTask();
        trailerTask.execute(mMovieID);
    }


    public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<String >> {

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
                Log.v(LOG_TAG, "Review entry: " + s);
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
                for(String reviewString : result){
                    mReviewAdapter.add(reviewString);
                }
            }
            if(result==null||result.isEmpty()){
                mReviewAdapter.add("No reviews in the movie database yet.");
            }

        }



    }



    public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<String >>{

        private ArrayList<String>getTrailerDataFromJson(String trailerJsonStr)
                throws JSONException

        {
            final String TMDB_YOUTUBE="results";
            final String TMDB_TRAILERS = "videos";
            final String TMDB_NAME= "name";
            final String TMDB_SOURCE="key";

            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONObject(TMDB_TRAILERS).getJSONArray(TMDB_YOUTUBE);

            ArrayList<String> resultStrs=new ArrayList<String>();

            for(int i=0; i<trailerArray.length();i++){
                String name;
                String source;


                JSONObject trailer =trailerArray.getJSONObject(i);

                name=trailer.getString(TMDB_NAME);
                source=trailer.getString(TMDB_SOURCE);

                resultStrs.add(name+"\n"+"https://www.youtube.com/watch?v="+source);


            }
            for (String s : resultStrs){
                Log.v(LOG_TAG, "Trailer entry: "+ s);
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

            String trailerJsonStr = null;

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
                    trailerJsonStr = null;
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
                trailerJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Trailer JSON String: " + trailerJsonStr);

            } catch (
                    IOException e
                    )

            {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                trailerJsonStr= null;
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
                return getTrailerDataFromJson(trailerJsonStr);
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
                mTrailerAdapter.clear();
                for(String reviewString : result){
                    mTrailerAdapter.add(reviewString);
                }
            }
            if(result==null||result.isEmpty()){
                mTrailerAdapter.add("No trailers for this film in the movie database yet.");
            }

        }



    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);

    }



}
