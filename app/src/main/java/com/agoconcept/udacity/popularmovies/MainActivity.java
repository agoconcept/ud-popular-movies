package com.agoconcept.udacity.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    private boolean mSortByPopularity = true;

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

        fetchMovies();
    }

    private void fetchMovies() {
        URL url;
        if (mSortByPopularity) {
            url = NetworkUtils.buildPopularMoviesQuery(this);
        }
        else {
            url = NetworkUtils.buildTopRatedMoviesQuery(this);
        }

        new TMDBQueryTask().execute(url);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // Check that item exists
        if (clickedItemIndex >= mMoviesList.size()) {
            Toast.makeText(this, getString(R.string.movie_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        PopularMovie movie = mMoviesList.get(clickedItemIndex);

        Intent intent = new Intent(MainActivity.this, MovieActivity.class);

        intent.putExtra("title", movie.getTitle());
        intent.putExtra("cover", movie.getCoverUri().toString());
        intent.putExtra("overview", movie.getOverview());
        intent.putExtra("user_rating", movie.getRating());
        intent.putExtra("release_date", movie.getReleaseDate());

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

                        String originalTitle = movieResult.getString("original_title");
                        String posterPath = NetworkUtils.getTmdbPosterBaseUrl() + movieResult.getString("poster_path");
                        String overview = movieResult.getString("overview");
                        Double rating = movieResult.getDouble("vote_average");
                        String releaseDate = movieResult.getString("release_date");

                        PopularMovie movie = new PopularMovie(originalTitle, Uri.parse(posterPath), overview, rating, releaseDate);

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
                mSortByPopularity = true;
                fetchMovies();
                return true;

            case R.id.action_sort_best_rated:
                mSortByPopularity = false;
                fetchMovies();
                return true;

            case R.id.action_show_favorites:
                Toast.makeText(MainActivity.this, "TODO: Favorites not implemented yet", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
