package com.agoconcept.udacity.popularmovies;

import android.net.Uri;

public class PopularMovie {

    private String mTitle;
    private Uri mCoverUri;
    private String mOverview;
    private Double mRating;
    private String mReleaseDate;

    public PopularMovie(String title, Uri coverUri, String overview, Double rating, String releaseDate) {
        mTitle = title;
        mCoverUri = coverUri;
        mOverview = overview;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public Uri getCoverUri() {
        return mCoverUri;
    }

    public String getOverview() { return mOverview; }

    public Double getRating() { return mRating; }

    public String getReleaseDate() { return mReleaseDate; }
}
