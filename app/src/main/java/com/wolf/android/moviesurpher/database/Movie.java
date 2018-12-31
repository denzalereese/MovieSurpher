package com.wolf.android.moviesurpher.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/* Movie class and Room database Entity */
@Entity(tableName="movie_table")
public class Movie {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "movie_id")
    private int mId;
    @ColumnInfo(name = "movie_poster_path")
    private String mPosterPath;
    @ColumnInfo(name = "movie_title")
    private String mTitle;
    @ColumnInfo(name = "movie_rating")
    private String mRating;
    @ColumnInfo(name = "movie_release_date")
    private String mReleaseDate;
    @ColumnInfo(name = "movie_overview")
    private String mOverview;

    public Movie(int id, String posterPath, String title, String rating,
                 String releaseDate, String overview) {
        this.mId = id;
        this.mPosterPath = posterPath;
        this.mTitle = title;
        this.mRating = rating;
        this.mReleaseDate = releaseDate.substring(0, 4);
        this.mOverview = overview;
    }


    public int getId() {
        return mId;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getOverview() {
        return mOverview;
    }
}
