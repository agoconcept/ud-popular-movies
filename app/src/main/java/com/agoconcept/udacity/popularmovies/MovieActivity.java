package com.agoconcept.udacity.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity extends AppCompatActivity {

    @BindView (R.id.iv_item_movie_cover) ImageView mCoverImageView;
    @BindView (R.id.tv_movie_original_title) TextView mTitleTextView;
    @BindView (R.id.tv_movie_overview) TextView mOverviewTextView;
    @BindView (R.id.tv_movie_user_rating) TextView mUserRatingTextView;
    @BindView (R.id.tv_movie_release_date) TextView mReleaseDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

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
