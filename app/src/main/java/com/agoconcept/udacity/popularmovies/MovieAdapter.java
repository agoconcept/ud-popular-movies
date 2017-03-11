package com.agoconcept.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<PopularMovie> mMovies;

    final private ListItemClickListener mOnClickListener;

    // Interface to handle clicking on items
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MovieAdapter(ArrayList<PopularMovie> movies, ListItemClickListener listener) {
        mMovies = movies;
        mOnClickListener = listener;
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

        public MovieViewHolder(View itemView) {
            super(itemView);

            mCoverImageView = (ImageView) itemView.findViewById(R.id.iv_item_movie_cover);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        public void bindMovie(PopularMovie movie) {

            Picasso.with(mCoverImageView.getContext())
                    .load(movie.getCoverUri())
                    .fit()
                    .placeholder(R.drawable.movie)
                    .error(R.drawable.alert)
                    .into(mCoverImageView);
        }
    }
}
