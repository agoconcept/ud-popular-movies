package com.agoconcept.udacity.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.agoconcept.udacity.popularmovies.util.NetworkUtils;
import com.agoconcept.udacity.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<String> mTrailers;

    final private ListItemClickListener mOnClickListener;

    // Interface to handle clicking on items
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public TrailerAdapter(ArrayList<String> trailers, ListItemClickListener listener) {
        mTrailers = trailers;
        mOnClickListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bindTrailer(mTrailers.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mTrailerImageView;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            mTrailerImageView = (ImageView) itemView.findViewById(R.id.iv_movie_trailer);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        public void bindTrailer(String id) {

            String uri = NetworkUtils.buildYouTubeVideoThumbnailUrl(id);

            Picasso.with(mTrailerImageView.getContext())
                    .load(uri)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.movie)
                    .error(R.drawable.alert)
                    .into(mTrailerImageView);
        }
    }
}
