package com.agoconcept.udacity.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieActivity extends AppCompatActivity {

    private ImageView mCoverImageView;
    private TextView mTitleTextView;
    private TextView mOverviewTextView;
    private TextView mUserRatingTextView;
    private TextView mReleaseDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mCoverImageView = (ImageView) findViewById(R.id.iv_item_movie_cover);
        mTitleTextView = (TextView) findViewById(R.id.tv_movie_original_title);
        mOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        mUserRatingTextView = (TextView) findViewById(R.id.tv_movie_user_rating);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);

        Intent intent = getIntent();

        if (intent.hasExtra("cover")) {
            String coverUri = intent.getStringExtra("cover");

            Picasso.with(mCoverImageView.getContext())
                    .load(coverUri)
                    .into(mCoverImageView);
        }

        if (intent.hasExtra("title")) {
            mTitleTextView.setText(intent.getStringExtra("title"));
        }

        if (intent.hasExtra("overview")) {
            mOverviewTextView.setText(intent.getStringExtra("overview"));
        }

        if (intent.hasExtra("user_rating")) {
            mUserRatingTextView.setText(String.valueOf(intent.getDoubleExtra("user_rating", 0.0)));
        }

        if (intent.hasExtra("release_date")) {
            mReleaseDateTextView.setText(intent.getStringExtra("release_date"));
        }
    }
}
