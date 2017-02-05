package com.agoconcept.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
            Toast.makeText(view.getContext(), "Movie clicked!", Toast.LENGTH_SHORT).show();
        }

        public void bindMovie(PopularMovie movie) {
            mTitleTextView.setText(movie.getTitle());

            Picasso.with(mCoverImageView.getContext())
                    .load(movie.getCoverUri())
                    .into(mCoverImageView);
        }
    }
}
