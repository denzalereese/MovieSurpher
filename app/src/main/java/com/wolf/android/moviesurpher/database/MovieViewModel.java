package com.wolf.android.moviesurpher.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

/* View Model - Lets the UI interact with the database */
public class MovieViewModel extends AndroidViewModel {

    private MovieRepository mRepository;
    private LiveData<List<Movie>> mAllMovies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        this.mRepository = new MovieRepository(application);
        this.mAllMovies = mRepository.getAllMovies();
    }

    public LiveData<List<Movie>> getAllMovies() {
        return mAllMovies;
    }

    public void insert(Movie movie) {
        mRepository.insert(movie);
    }
}
