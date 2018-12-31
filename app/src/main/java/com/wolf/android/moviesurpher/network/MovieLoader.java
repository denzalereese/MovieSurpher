package com.wolf.android.moviesurpher.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;


public class MovieLoader extends AsyncTaskLoader<String> {

    private int mMovieId;
    private String mCategory;

    public static final String POPULAR_MOVIES = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String MOVIE_TRAILERS = "trailers";
    public static final String MOVIE_REVIEWS = "reviews";


    public MovieLoader(@NonNull Context context, String category) {
        super(context);
        this.mCategory = category;
    }

    public MovieLoader(@NonNull Context context, String category, int movieId) {
        super(context);
        this.mMovieId = movieId;
        this.mCategory = category;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        //make request to API at appropriate endpoint
        switch(mCategory) {
            case POPULAR_MOVIES:
                return NetworkUtils.getPopularMovies();
            case TOP_RATED:
                return NetworkUtils.getTopRatedMovies();
            case MOVIE_TRAILERS:
                return NetworkUtils.getMovieTrailers(mMovieId);
            case MOVIE_REVIEWS:
                return NetworkUtils.getMovieReviews(mMovieId);
        }
        return null;
    }

}
