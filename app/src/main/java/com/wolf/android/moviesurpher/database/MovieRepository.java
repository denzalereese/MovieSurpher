package com.wolf.android.moviesurpher.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/* Repository - executes database methods off the UI thread */
public class MovieRepository {
    private MovieDao mMovieDao;
    private LiveData<List<Movie>> mAllMovies;

    public MovieRepository(Application application) {
        MovieRoomDatabase db = MovieRoomDatabase.getDatabase(application);
        mMovieDao = db.movieDao();
        mAllMovies = mMovieDao.getAllMovies();
    }

    LiveData<List<Movie>> getAllMovies() {
        return mAllMovies;
    }

    public void insert(Movie movie) {
        new insertAsyncTask(mMovieDao).execute(movie);
    }

    private class insertAsyncTask extends AsyncTask<Movie, Void, Void> {
        private MovieDao mAsyncTaskDao;

        public insertAsyncTask(MovieDao dao) {
            this.mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Movie... movies) {
            mAsyncTaskDao.insert(movies[0]);
            return null;
        }
    }

}
