package com.example.drew.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by drew on 9/7/15.
 */
public class MovieFragment extends Fragment {
    private static final String LOG_TAG = GridViewActivity.class.getSimpleName();

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<Movie> mMovie;
    private String mBase_URL = "http://api.themoviedb.org/3/discover/movie?";
    private String mSort = null;
    private String mApi_key = "&api_key=bb99fbc46e9777b057575f946a19f3f3";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    View rootView=inflater.inflate(R.layout.activity_gridview,container, false);
    mGridView = (GridView) rootView.findViewById(R.id.gridView);

    mMovie = new ArrayList<>();
    mGridAdapter = new GridViewAdapter(getActivity(), R.layout.movie_layout, mMovie);
    mGridView.setAdapter(mGridAdapter);

    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            Movie movie = (Movie) parent.getItemAtPosition(position);
            Intent intent = new Intent(getActivity(), DetailsActivity.class);

            intent.putExtra("title", movie.getTitle()).
                    putExtra("image", movie.getImage()).
                    putExtra("year", movie.getYear()).
                    putExtra("description", movie.getDesc()).
                    putExtra("rating", movie.getRating()).
                    putExtra("id", movie.getMovieID());
            startActivity(intent);
        }
    });
        return rootView;
}


    public void updateMovies() {
        mGridAdapter.clear();
        AsyncHttpTask movieTask = new AsyncHttpTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSort = prefs.getString("sort", "sort_by=popularity.desc");


            String fullPath = mBase_URL + mSort + mApi_key;
            movieTask.execute(fullPath);




    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

    @Override
    protected Integer doInBackground(String... params) {
        Integer result = 0;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
            int status = httpResponse.getStatusLine().getStatusCode();

            if (status == 200){

                if(mSort.startsWith("sort_by")){
                String response = streamToString(httpResponse.getEntity().getContent());
                parseResult(response);
                Log.v(LOG_TAG,"response to Parse method: "+response);
                result = 1;}

                else{
                    Movie movie =new Movie("Avengers: Age of Ultron","99861","2015-05-01","7.7",
                            "When Tony Stark tries to jumpstart a dormant peacekeeping program, things go awry and Earth’s Mightiest Heroes are put to the ultimate test as the fate of the planet hangs in the balance. As the villainous Ultron emerges, it is up to The Avengers to stop him from enacting his terrible plans, and soon uneasy alliances and unexpected action pave the way for an epic and unique global adventure.",
                            "/t90Y3G8UGQp0f0DrP60wRu9gfrH.jpg");
                    Movie movie2 =new Movie("Dino","99861","2015-05-01","7.7",
                            "Howdy",
                            "/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");


                    String testStr="{\"results\":["+movie.toString()+","+movie2.toString()+"]}";
                    Log.v(LOG_TAG,"fav to parse results: "+testStr);
                    parseResult(testStr);

                    result=1;

                }



            } else {
                result = 0;
                Log.v(LOG_TAG,"mSort: "+mSort);
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getLocalizedMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {

        if (result == 1) {
            mGridAdapter.setGridData(mMovie);
        } else {
            Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
        }

    }
}

    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        if (null != stream) {
            stream.close();
        }
        return result;
    }


    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("results");
            Movie movie;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                String poster = post.optString("poster_path");
                String fullPosterPath = "http://image.tmdb.org/t/p/w500/" + poster;
                String year = post.optString("release_date");
                String desc = post.optString("overview");
                String rating = post.optString("vote_average");
                String id =post.optString("id");

                movie = new Movie();
                movie.setTitle(title);
                movie.setYear(year);
                movie.setDesc(desc);
                movie.setRating(rating);
                movie.setImage(fullPosterPath);
                movie.setMovieID(id);
                Log.v(LOG_TAG,"movie id: "+id +"poster_path: "+fullPosterPath);
                mMovie.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}

