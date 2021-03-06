package com.agoconcept.udacity.popularmovies.util;

import android.content.Context;
import android.net.Uri;

import com.agoconcept.udacity.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    // One of "w92", "w154", "w185", "w342", "w500", "w780", or "original"
    private static final String TMDB_POSTER_QUALITY = "w500";

    private static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String TMDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String TMDB_POPULAR_MOVIES_PATH = "/popular";
    private static final String TMDB_TOP_RATED_MOVIES_PATH = "/top_rated";

    private static final String TMDB_MOVIE_TRAILERS_PATH = "/videos";
    private static final String TMDB_MOVIE_REVIEWS_PATH = "/reviews";

    private static final String TMDB_API_KEY_PARAM = "api_key";

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private static final String YOUTUBE_VIDEO_THUMBNAIL_PREFIX_URL = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_VIDEO_THUMBNAIL_SUFFIX_URL = "/hqdefault.jpg";


    public static URL buildPopularMoviesQuery(Context context) {
        String baseUrl = TMDB_BASE_URL + TMDB_POPULAR_MOVIES_PATH;
        return buildQuery(baseUrl, context);
    }

    public static URL buildTopRatedMoviesQuery(Context context) {
        String baseUrl = TMDB_BASE_URL + TMDB_TOP_RATED_MOVIES_PATH;
        return buildQuery(baseUrl, context);
    }

    public static URL buildMovieTrailersQuery(String movieId, Context context) {
        String baseUrl = TMDB_BASE_URL + "/" + movieId + TMDB_MOVIE_TRAILERS_PATH;
        return buildQuery(baseUrl, context);
    }

    public static URL buildMovieReviewsQuery(String movieId, Context context) {
        String baseUrl = TMDB_BASE_URL + "/" + movieId + TMDB_MOVIE_REVIEWS_PATH;
        return buildQuery(baseUrl, context);
    }

    private static URL buildQuery(String baseUrl, Context context) {
        String apiKey = context.getResources().getString(R.string.api_key);

        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(TMDB_API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getTmdbPosterBaseUrl() { return TMDB_POSTER_BASE_URL + TMDB_POSTER_QUALITY; }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Uri buildYouTubeVideoUri(String movieId) {
        String url = YOUTUBE_BASE_URL + movieId;
        return Uri.parse(url);
    }

    public static String buildYouTubeVideoThumbnailUrl(String movieId) {
        return YOUTUBE_VIDEO_THUMBNAIL_PREFIX_URL + movieId + YOUTUBE_VIDEO_THUMBNAIL_SUFFIX_URL;
    }
}
