package com.agoconcept.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<PopularMovie> mMovies;

    public MovieAdapter(ArrayList<PopularMovie> movies) {
        mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        PopularMovie movieItem = mMovies.get(position);
        holder.bindMovie(movieItem);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private PopularMovie mMovie;

        private ImageView mCoverImageView;
        private TextView mTitleTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mCoverImageView = (ImageView) itemView.findViewById(R.id.iv_movie_cover);
            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_movie_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // TODO
            Intent intent = new Intent(view.getContext(), MovieActivity.class);

            intent.putExtra("title", mMovie.getTitle());
            intent.putExtra("cover", mMovie.getCoverUri().toString());
            intent.putExtra("overview", mMovie.getOverview());
            intent.putExtra("user_rating", mMovie.getRating());
            intent.putExtra("release_date", mMovie.getReleaseDate());

            // TODO: Start activity
        }

        public void bindMovie(PopularMovie movie) {
            mMovie = movie;

            mTitleTextView.setText(movie.getTitle());

            Picasso.with(mCoverImageView.getContext())
                    .load(movie.getCoverUri())
                    .into(mCoverImageView);
        }
    }
}
