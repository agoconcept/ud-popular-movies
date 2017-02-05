package com.agoconcept.udacity.popularmovies;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mMainLayoutRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MovieAdapter mMovieAdapter;

    private ArrayList<PopularMovie> mMoviesList;

    private static final int GRID_NUMBER_OF_COLUMNS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up grid layout
        mMainLayoutRecyclerView = (RecyclerView) findViewById(R.id.rv_main_layout);
        mGridLayoutManager = new GridLayoutManager(this, GRID_NUMBER_OF_COLUMNS);
        mMainLayoutRecyclerView.setLayoutManager(mGridLayoutManager);

        mMoviesList = new ArrayList<>();

        mMovieAdapter = new MovieAdapter(mMoviesList);
        mMainLayoutRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mMoviesList.size() == 0) {
            fetchMovies();
        }
    }

    private void fetchMovies() {

        // TODO
        String title = "La Loca Historia de las Galaxias";
        Uri uri = Uri.parse("https://images-na.ssl-images-amazon.com/images/M/MV5BMTM3Mzg0Mzc2NF5BMl5BanBnXkFtZTcwNDEwNTk0NA@@._V1_SY1000_CR0,0,666,1000_AL_.jpg");

        PopularMovie movie = new PopularMovie(title, uri);

        mMoviesList.add(movie);


        // TODO
        String title2 = "Loca Academia de Policia";
        Uri uri2 = Uri.parse("https://images-na.ssl-images-amazon.com/images/M/MV5BMjNiMWVhNjAtMzgyYS00NzRhLWJmNGUtNzdiOGFhMmY5NDUwL2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SY1000_CR0,0,661,1000_AL_.jpg");

        PopularMovie movie2 = new PopularMovie(title2, uri2);

        mMoviesList.add(movie2);


        // TODO: asynctask and all that stuff
    }
}
