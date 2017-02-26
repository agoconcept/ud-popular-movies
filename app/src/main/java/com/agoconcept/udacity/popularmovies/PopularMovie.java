package com.agoconcept.udacity.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PopularMovie implements Parcelable {

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

    protected PopularMovie(Parcel in) {
        mTitle = in.readString();
        mCoverUri = in.readParcelable(Uri.class.getClassLoader());
        mOverview = in.readString();
        mRating = in.readDouble();
        mReleaseDate = in.readString();
    }

    public static final Creator<PopularMovie> CREATOR = new Creator<PopularMovie>() {
        @Override
        public PopularMovie createFromParcel(Parcel in) {
            return new PopularMovie(in);
        }

        @Override
        public PopularMovie[] newArray(int size) {
            return new PopularMovie[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public Uri getCoverUri() {
        return mCoverUri;
    }

    public String getOverview() { return mOverview; }

    public Double getRating() { return mRating; }

    public String getReleaseDate() { return mReleaseDate; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeParcelable(mCoverUri, i);
        parcel.writeString(mOverview);
        parcel.writeDouble(mRating);
        parcel.writeString(mReleaseDate);
    }
}
