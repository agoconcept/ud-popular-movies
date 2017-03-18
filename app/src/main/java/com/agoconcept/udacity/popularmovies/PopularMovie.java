package com.agoconcept.udacity.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PopularMovie implements Parcelable {

    private String mId;
    private String mTitle;
    private Uri mCoverUri;
    private Uri mBackdropUri;
    private String mOverview;
    private double mRating;
    private int mVoteCount;
    private String mReleaseDate;

    // These are stored when viewing the movie
    private ArrayList<String> mTrailers;
    private ArrayList<String> mReviews;

    public PopularMovie(String id, String title, Uri coverUri, Uri backdropUri, String overview,
                        double rating, int voteCount, String releaseDate) {
        mId = id;
        mTitle = title;
        mCoverUri = coverUri;
        mBackdropUri = backdropUri;
        mOverview = overview;
        mRating = rating;
        mVoteCount = voteCount;
        mReleaseDate = releaseDate;

        mTrailers = new ArrayList<>();
        mReviews = new ArrayList<>();
    }

    protected PopularMovie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mCoverUri = in.readParcelable(Uri.class.getClassLoader());
        mBackdropUri = in.readParcelable(Uri.class.getClassLoader());
        mOverview = in.readString();
        mRating = in.readDouble();
        mVoteCount = in.readInt();
        mReleaseDate = in.readString();

        mTrailers = in.createStringArrayList();
        mReviews = in.createStringArrayList();
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

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Uri getCoverUri() {
        return mCoverUri;
    }

    public Uri getBackdropUri() {
        return mBackdropUri;
    }

    public String getOverview() { return mOverview; }

    public double getRating() { return mRating; }

    public int getVoteCount() { return mVoteCount; }

    public String getReleaseDate() { return mReleaseDate; }

    public ArrayList<String> getTrailers() { return mTrailers; }

    public ArrayList<String> getReviews() { return mReviews; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mTitle);
        parcel.writeParcelable(mCoverUri, i);
        parcel.writeParcelable(mBackdropUri, i);
        parcel.writeString(mOverview);
        parcel.writeDouble(mRating);
        parcel.writeInt(mVoteCount);
        parcel.writeString(mReleaseDate);

        parcel.writeStringList(mTrailers);
        parcel.writeStringList(mReviews);
    }
}
