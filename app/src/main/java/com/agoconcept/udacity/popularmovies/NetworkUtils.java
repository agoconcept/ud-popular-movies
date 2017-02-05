package com.agoconcept.udacity.popularmovies;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String TMDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342/";

    private static final String TMDB_POPULAR_MOVIES_PATH = "/popular";
    private static final String TMDB_TOP_RATED_MOVIES_PATH = "/top_rated";

    private static final String TMDB_API_KEY_PARAM = "api_key";

    public static URL buildPopularMoviesQuery(Context context) {
        String baseUrl = TMDB_BASE_URL + TMDB_POPULAR_MOVIES_PATH;
        return buildQuery(baseUrl, context);
    }

    public static URL buildTopRatedMoviesQuery(Context context) {
        String baseUrl = TMDB_BASE_URL + TMDB_TOP_RATED_MOVIES_PATH;
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

    public static final String getTmdbPosterBaseUrl() { return TMDB_POSTER_BASE_URL; }

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
}
