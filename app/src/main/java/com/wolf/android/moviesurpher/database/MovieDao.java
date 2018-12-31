package com.wolf.android.moviesurpher.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/* Data Access Object - SQL queries for database methods*/
@Dao
public interface MovieDao {

    @Insert
    void insert(Movie movie);

    @Query("SELECT * FROM movie_table")
    LiveData<List<Movie>> getAllMovies();
}
