package com.example.drew.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailsActivity extends ActionBarActivity {
    private TextView titleTextView;
    private ImageView imageView;


    private TextView descTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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

        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(titleYear);
        descTextView = (TextView) findViewById(R.id.desc);
        descTextView.setText(desc);
        descTextView.setMovementMethod(new ScrollingMovementMethod());

        imageView = (ImageView) findViewById(R.id.movie_image);
        Picasso.with(this).load(image).into(imageView);
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