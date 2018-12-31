package com.wolf.android.moviesurpher.network;

import android.net.Uri;

import com.wolf.android.moviesurpher.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String POPULAR_MOVIES_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String TRAILERS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";
    private static final String API_KEY_PARAM = "api_key";
    private static String API_KEY = Config.MOVIEDB_API_KEY;

    /* Gets movies at popular movies API endpoint */
    static String getPopularMovies() {
        Uri builtMovieUri = Uri.parse(NetworkUtils.MOVIE_BASE_URL).buildUpon()
                .appendPath(POPULAR_MOVIES_PATH)
                .appendQueryParameter(NetworkUtils.API_KEY_PARAM, API_KEY)
                .build();

        return getMovieJSON(builtMovieUri.toString());
    }

    /* Gets movies at top rated movies API endpoint */
    static String getTopRatedMovies() {
        Uri builtMovieUri = Uri.parse(NetworkUtils.MOVIE_BASE_URL).buildUpon()
                .appendPath(TOP_RATED_PATH)
                .appendQueryParameter(NetworkUtils.API_KEY_PARAM, API_KEY)
                .build();

        return getMovieJSON(builtMovieUri.toString());
    }

    /* Gets movie trailers from API for movie with the given ID */
    static String getMovieTrailers(int movieId) {
        Uri builtMovieUri = Uri.parse(NetworkUtils.MOVIE_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(TRAILERS_PATH)
                .appendQueryParameter(NetworkUtils.API_KEY_PARAM, API_KEY)
                .build();

        return getMovieJSON(builtMovieUri.toString());
    }

    /* Gets movie reviews from API for movie with the given ID */
    static String getMovieReviews(int movieId) {
        Uri builtMovieUri = Uri.parse(NetworkUtils.MOVIE_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(NetworkUtils.API_KEY_PARAM, API_KEY)
                .build();

        return getMovieJSON(builtMovieUri.toString());
    }

    /* Makes GET request to the given url and returns the JSON response as a String */
    static String getMovieJSON(String url) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String movieJSONString = null;

        try {
            URL requestURL = new URL(url);

            httpURLConnection = (HttpURLConnection) requestURL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            if(stringBuilder.length() == 0) {
                return null;
            }
            movieJSONString = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if(bufferedReader != null) {

                try {

                    bufferedReader.close();

                } catch(IOException e) {

                    e.printStackTrace();
                }
            }

        }
        return movieJSONString;
    }

}
