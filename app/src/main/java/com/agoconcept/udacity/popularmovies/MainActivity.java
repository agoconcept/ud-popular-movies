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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private RecyclerView mMainLayoutRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MovieAdapter mMovieAdapter;
    private TextView mSortedByTextView;
    private ProgressBar mLoadingIndicator;

    private ArrayList<PopularMovie> mMoviesList;

    private boolean mSortByPopularity = true;

    private static final int GRID_NUMBER_OF_COLUMNS_PORTRAIT = 2;
    private static final int GRID_NUMBER_OF_COLUMNS_LANDSCAPE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up grid layout (2 columns in portrait mode; 3 columns in landscape mode)
        mMainLayoutRecyclerView = (RecyclerView) findViewById(R.id.rv_main_layout);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mGridLayoutManager = new GridLayoutManager(this, GRID_NUMBER_OF_COLUMNS_LANDSCAPE);
        } else {
            mGridLayoutManager = new GridLayoutManager(this, GRID_NUMBER_OF_COLUMNS_PORTRAIT);
        }
        mMainLayoutRecyclerView.setLayoutManager(mGridLayoutManager);

        mMoviesList = new ArrayList<>();

        mMovieAdapter = new MovieAdapter(mMoviesList, this);
        mMainLayoutRecyclerView.setAdapter(mMovieAdapter);

        mSortedByTextView = (TextView) findViewById(R.id.tv_main_sorted_by);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_main_loading_indicator);

        fetchMovies();
    }

    private void fetchMovies() {
        mMoviesList.clear();

        URL url;
        if (mSortByPopularity) {
            mSortedByTextView.setText(getString(R.string.sorted_by_popularity));
            url = NetworkUtils.buildPopularMoviesQuery(this);
        }
        else {
            mSortedByTextView.setText(getString(R.string.sorted_by_top_rated));
            url = NetworkUtils.buildTopRatedMoviesQuery(this);
        }

        new TMDBQueryTask().execute(url);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
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
                    Toast.makeText(MainActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Unable to get response from server. Are you connected to the Internet?", Toast.LENGTH_SHORT).show();
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
            case R.id.action_toggle_sort:
                mSortByPopularity = !mSortByPopularity;
                fetchMovies();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
