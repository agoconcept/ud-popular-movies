package com.agoconcept.udacity.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agoconcept.udacity.popularmovies.adapter.MovieAdapter;
import com.agoconcept.udacity.popularmovies.data.MovieContract;
import com.agoconcept.udacity.popularmovies.data.MovieDBHelper;
import com.agoconcept.udacity.popularmovies.data.PopularMovie;
import com.agoconcept.udacity.popularmovies.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    @BindView (R.id.pb_main_loading_indicator) ProgressBar mLoadingIndicator;

    @BindView (R.id.rv_main_layout) RecyclerView mMainLayoutRecyclerView;

    private GridLayoutManager mGridLayoutManager;
    private MovieAdapter mMovieAdapter;

    private ArrayList<PopularMovie> mMoviesList;

    static final String STATE_SORT_CRITERIA = "SORT_CRITERIA";

    //  Not using an enum to facilitate storing it in a SharedPreferences
    static final int SORT_POPULARITY = 0;
    static final int SORT_BEST_RATED = 1;
    static final int SORT_FAVORITES = 2;
    private int mSortCriteria = SORT_POPULARITY;

    private static final int GRID_NUMBER_OF_COLUMNS_PORTRAIT = 2;
    private static final int GRID_NUMBER_OF_COLUMNS_LANDSCAPE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Set up grid layout (2 columns in portrait mode; 3 columns in landscape mode)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mGridLayoutManager = new GridLayoutManager(this, GRID_NUMBER_OF_COLUMNS_LANDSCAPE);
        } else {
            mGridLayoutManager = new GridLayoutManager(this, GRID_NUMBER_OF_COLUMNS_PORTRAIT);
        }
        mMainLayoutRecyclerView.setLayoutManager(mGridLayoutManager);

        mMoviesList = new ArrayList<>();

        mMovieAdapter = new MovieAdapter(mMoviesList, this);
        mMainLayoutRecyclerView.setAdapter(mMovieAdapter);

        // Restore the previous criteria
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSortCriteria = prefs.getInt(STATE_SORT_CRITERIA, SORT_POPULARITY);

        fetchMovies();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Save the current criteria
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(STATE_SORT_CRITERIA, mSortCriteria);
        edit.apply();
    }

    private void fetchMovies() {
        URL url;
        if (mSortCriteria == SORT_POPULARITY) {
            url = NetworkUtils.buildPopularMoviesQuery(this);
            new TMDBQueryTask().execute(url);
        }
        else if (mSortCriteria == SORT_BEST_RATED) {
            url = NetworkUtils.buildTopRatedMoviesQuery(this);
            new TMDBQueryTask().execute(url);
        }
        else if (mSortCriteria == SORT_FAVORITES) {
            getFavoriteMovies();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // Check that item exists
        if (clickedItemIndex >= mMoviesList.size()) {
            Toast.makeText(this, getString(R.string.movie_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, MovieActivity.class);

        PopularMovie movie = mMoviesList.get(clickedItemIndex);

        intent.putExtra("movie", movie);

        startActivity(intent);
    }

    public class TMDBQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (tmdbSearchResults != null && !tmdbSearchResults.equals("")) {
                try {
                    JSONObject jsonResults = new JSONObject(tmdbSearchResults);

                    JSONArray movieResults = jsonResults.getJSONArray("results");

                    mMoviesList.clear();

                    for (int i = 0; i < movieResults.length(); i++) {
                        JSONObject movieResult = movieResults.getJSONObject(i);

                        String id = movieResult.getString("id");

                        String title = movieResult.getString("title");
                        String posterPath = NetworkUtils.getTmdbPosterBaseUrl() + movieResult.getString("poster_path");
                        String overview = movieResult.getString("overview");
                        double rating = movieResult.getDouble("vote_average");
                        int voteCount = movieResult.getInt("vote_count");
                        String releaseDate = movieResult.getString("release_date");

                        PopularMovie movie = new PopularMovie(
                                id,
                                title,
                                Uri.parse(posterPath),
                                overview,
                                rating,
                                voteCount,
                                releaseDate,
                                false);         // Do not mark it as favorite

                        mMoviesList.add(movie);
                    }

                    mMovieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, getString(R.string.invalid_response), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.no_response), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_sort_popularity:
                mSortCriteria = SORT_POPULARITY;
                fetchMovies();
                return true;

            case R.id.action_sort_best_rated:
                mSortCriteria = SORT_BEST_RATED;
                fetchMovies();
                return true;

            case R.id.action_show_favorites:
                mSortCriteria = SORT_FAVORITES;
                fetchMovies();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFavoriteMovies() {

        Cursor cursor = getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_TITLE);

        mMoviesList.clear();

        // Iterate for all elements retrieved from the DB and add them to the list of movies
        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TMDB_ID));

            String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_COVER_URI));
            String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            int voteCount = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
            String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));

            PopularMovie movie = new PopularMovie(
                    id,
                    title,
                    Uri.parse(posterPath),
                    overview,
                    rating,
                    voteCount,
                    releaseDate,
                    true);         // Mark it as favorite

            mMoviesList.add(movie);
        }

        cursor.close();

        mMovieAdapter.notifyDataSetChanged();
    }
}
