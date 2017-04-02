package com.agoconcept.udacity.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity
        extends AppCompatActivity
        implements TrailerAdapter.ListItemClickListener {

    @BindView (R.id.iv_item_movie_cover) ImageView mCoverImageView;
    @BindView (R.id.tv_movie_original_title) TextView mTitleTextView;
    @BindView (R.id.tv_movie_overview) TextView mOverviewTextView;
    @BindView (R.id.tv_movie_user_rating) TextView mUserRatingTextView;
    @BindView (R.id.tv_movie_release_date) TextView mReleaseDateTextView;

    @BindView (R.id.pb_movie_trailers) ProgressBar mTrailersLoadingIndicator;
    @BindView (R.id.pb_movie_reviews) ProgressBar mReviewsLoadingIndicator;

    @BindView (R.id.rv_movie_trailers) RecyclerView mTrailersRecyclerView;
    @BindView (R.id.rv_movie_reviews) RecyclerView mReviewsRecyclerView;

    private LinearLayoutManager mTrailersLayoutManager;
    private TrailerAdapter mTrailerAdapter;

    private LinearLayoutManager mReviewsLayoutManager;
    private ReviewAdapter mReviewAdapter;

    private PopularMovie mMovie;

    private SQLiteDatabase mDb;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        // This is to enable scrolling for long titles
        mTitleTextView.setMovementMethod(new ScrollingMovementMethod());

        // Set SQLite DB
        MovieDBHelper dbHelper = new MovieDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        Intent intent = getIntent();

        if (intent.hasExtra("movie")) {

            mMovie = intent.getParcelableExtra("movie");

            String coverUri = mMovie.getCoverUri().toString();
            Picasso.with(mCoverImageView.getContext())
                    .load(coverUri)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.movie)
                    .error(R.drawable.alert)
                    .into(mCoverImageView);

            mTitleTextView.setText(mMovie.getTitle());

            mOverviewTextView.setText(mMovie.getOverview());

            String rating = String.valueOf(mMovie.getRating()) + "/10";
            mUserRatingTextView.setText(rating);

            // Extract the year
            if (mMovie.getReleaseDate().length() >= 4) {
                String year = mMovie.getReleaseDate().substring(0, 4);
                mReleaseDateTextView.setText(year);
            } else {
                mReleaseDateTextView.setText("-");
            }
        }

        mTrailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersRecyclerView.setLayoutManager(mTrailersLayoutManager);
        mTrailerAdapter = new TrailerAdapter(mMovie.getTrailers(), this);
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);

        mReviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager);
        mReviewAdapter = new ReviewAdapter(mMovie.getReviews());
        mReviewsRecyclerView.setAdapter(mReviewAdapter);

        // This is to add dividers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mReviewsRecyclerView.getContext(),
                mReviewsLayoutManager.getOrientation());
        mReviewsRecyclerView.addItemDecoration(dividerItemDecoration);

        // This is to scroll smoothly
        mReviewsRecyclerView.setNestedScrollingEnabled(false);

        // Fetch trailers and reviews
        fetchTrailers();
        fetchReviews();
    }

    private void fetchTrailers() {
        URL url = NetworkUtils.buildMovieTrailersQuery(mMovie.getId(), this);
        new MovieActivity.TMDBTrailersTask().execute(url);
    }

    private void fetchReviews() {
        URL url = NetworkUtils.buildMovieReviewsQuery(mMovie.getId(), this);
        new MovieActivity.TMDBReviewsTask().execute(url);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // Check that item exists
        if (clickedItemIndex >= mMovie.getTrailers().size()) {
            Toast.makeText(this, getString(R.string.trailer_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        Uri trailerUri = NetworkUtils.buildYouTubeVideoUri(mMovie.getTrailers().get(clickedItemIndex));

        // Start trailer on YouTube
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        intent.putExtra("force_fullscreen", true);

        // Check if there's an activity that can handle it, or fail otherwise
        PackageManager manager = getPackageManager();
        List<ResolveInfo> activities = manager.queryIntentActivities(intent, 0);
        if (activities.size() > 0) {
            startActivity(intent);
        } else {
            Toast.makeText(MovieActivity.this, getString(R.string.no_app_for_video_intent), Toast.LENGTH_SHORT).show();
        }
    }

    public class TMDBTrailersTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTrailersLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String tmdbSearchResults = null;
            try {
                tmdbSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tmdbSearchResults;
        }

        @Override
        protected void onPostExecute(String tmdbSearchResults) {
            mTrailersLoadingIndicator.setVisibility(View.INVISIBLE);
            if (tmdbSearchResults != null && !tmdbSearchResults.equals("")) {
                try {
                    JSONObject jsonResults = new JSONObject(tmdbSearchResults);

                    JSONArray trailersResults = jsonResults.getJSONArray("results");

                    ArrayList<String> trailers = mMovie.getTrailers();
                    trailers.clear();

                    for (int i = 0; i < trailersResults.length(); i++) {
                        JSONObject trailer = trailersResults.getJSONObject(i);

                        // Only trailers from YouTube supported
                        if (trailer.getString("site").equals("YouTube")) {
                            trailers.add(trailer.getString("key"));
                        }
                    }

                    mTrailerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(MovieActivity.this, getString(R.string.invalid_response), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MovieActivity.this, getString(R.string.no_response), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class TMDBReviewsTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mReviewsLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String tmdbSearchResults = null;
            try {
                tmdbSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tmdbSearchResults;
        }

        @Override
        protected void onPostExecute(String tmdbSearchResults) {
            mReviewsLoadingIndicator.setVisibility(View.INVISIBLE);
            if (tmdbSearchResults != null && !tmdbSearchResults.equals("")) {
                try {
                    JSONObject jsonResults = new JSONObject(tmdbSearchResults);

                    JSONArray reviewResults = jsonResults.getJSONArray("results");

                    ArrayList<String> reviews = mMovie.getReviews();
                    reviews.clear();

                    for (int i = 0; i < reviewResults.length(); i++) {
                        JSONObject reviewResult = reviewResults.getJSONObject(i);

                        String author = reviewResult.getString("author");
                        String content = reviewResult.getString("content");

                        reviews.add(content);
                    }

                    mReviewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(MovieActivity.this, getString(R.string.invalid_response), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MovieActivity.this, getString(R.string.no_response), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.movie, menu);
        setFavoriteIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_toggle_favorite:

                // Toggle icon
                mMovie.setIsFavorite(!mMovie.getIsFavorite());
                setFavoriteIcon();

                // Update movie in SQLite
                if (mMovie.getIsFavorite())
                    addToFavoriteMovies();
                else
                    removeFromFavoriteMovies();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private long addToFavoriteMovies() {
        ContentValues cv = new ContentValues();

        cv.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, mMovie.getId());

        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_COVER_URI, mMovie.getCoverUri().toString());
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
        cv.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getRating());
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, mMovie.getVoteCount());
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());

        long rowId = mDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);

        if (rowId != -1)
            Toast.makeText(MovieActivity.this, getString(R.string.movie_saved_as_favorite), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MovieActivity.this, getString(R.string.movie_error_saving_favorite), Toast.LENGTH_SHORT).show();

        return rowId;
    }

    private void removeFromFavoriteMovies() {
        int rowsDeleted = mDb.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_TMDB_ID + "=" + mMovie.getId(), null);

        if (rowsDeleted > 0)
            Toast.makeText(MovieActivity.this, getString(R.string.movie_deleted_as_favorite), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MovieActivity.this, getString(R.string.movie_error_deleting_favorite), Toast.LENGTH_SHORT).show();
    }

    private void setFavoriteIcon() {
        if (mMovie.getIsFavorite())
            mMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.heart));
        else
            mMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.heart_outline));
    }
}
