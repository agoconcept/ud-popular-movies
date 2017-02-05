package com.agoconcept.udacity.popularmovies;

import android.net.Uri;

public class PopularMovie {

    private String mTitle;
    private Uri mCoverUri;

    public PopularMovie(String title, Uri coverUri) {
        mTitle = title;
        mCoverUri = coverUri;
    }

    public String getTitle() {
        return mTitle;
    }

    public Uri getCoverUri() {
        return mCoverUri;
    }
}
