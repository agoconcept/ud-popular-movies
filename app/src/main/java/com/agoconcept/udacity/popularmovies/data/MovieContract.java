package com.agoconcept.udacity.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by egalsan on 02/04/2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.agoconcept.udacity.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        private MovieEntry() {};

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TMDB_ID = "tmdb_id";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_COVER_URI = "cover_uri";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_TRAILERS = "trailers";
        public static final String COLUMN_REVIEWS = "reviews";
    }
}
