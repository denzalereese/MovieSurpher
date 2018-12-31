package com.wolf.android.moviesurpher.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.wolf.android.moviesurpher.R;
import com.wolf.android.moviesurpher.database.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MoviePosterAdapter
        extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterViewHolder> {

   public static final String MOVIE_ID_KEY = "id";
   public static final String MOVIE_POSTER_PATH_KEY = "poster_path";
   public static final String MOVIE_TITLE_KEY = "title";
   public static final String MOVIE_RATING_KEY = "vote_average";
   public static final String MOVIE_OVERVIEW_KEY = "overview";
   public static final String MOVIE_RELEASE_DATE_KEY = "release_date";

    private List<Movie> mMoviesList;
    private LayoutInflater mInflater;
    private Context mContext;

    public MoviePosterAdapter(Context context, JSONArray moviesArray) {
        this.mInflater = LayoutInflater.from(context);
        this.mMoviesList = createMoviesListFromJson(moviesArray);
        this.mContext = context;
    }

    public class MoviePosterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView moviePosterImageView;
        MoviePosterAdapter moviePosterAdapter;

        public MoviePosterViewHolder(@NonNull View itemView,
                                     MoviePosterAdapter moviePosterAdapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.moviePosterImageView = itemView.findViewById(R.id.movie_poster);
            this.moviePosterAdapter = moviePosterAdapter;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Intent intentToOpenDetailActivity = new Intent(mContext, MovieDetailActivity.class);
            //store movie details in intent Bundle to be sent to DetailActivity
            Bundle currentMovieBundle = new Bundle();
            Movie currentMovie = mMoviesList.get(position);
            currentMovieBundle.putInt(MOVIE_ID_KEY, currentMovie.getId());
            currentMovieBundle.putString(MOVIE_POSTER_PATH_KEY, currentMovie.getPosterPath());
            currentMovieBundle.putString(MOVIE_TITLE_KEY, currentMovie.getTitle());
            currentMovieBundle.putString(MOVIE_RATING_KEY, currentMovie.getRating());
            currentMovieBundle.putString(MOVIE_RELEASE_DATE_KEY, currentMovie.getReleaseDate());
            currentMovieBundle.putString(MOVIE_OVERVIEW_KEY, currentMovie.getOverview());
            intentToOpenDetailActivity.putExtras(currentMovieBundle);
            //open DetailActivity
            mContext.startActivity(intentToOpenDetailActivity);
        }
    }

    @NonNull
    @Override
    public MoviePosterAdapter.MoviePosterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                                       int i) {
        View itemView = mInflater.inflate(R.layout.movie_poster_item, viewGroup, false);
        return new MoviePosterViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MoviePosterAdapter.MoviePosterViewHolder moviePosterViewHolder, int i) {
            Movie currentMovie = mMoviesList.get(i);
            //load movie poster into ViewHolder's ImageView
           //use Picasso library to load images for better memory management/automatic caching
            Picasso.with(mContext).load(currentMovie.getPosterPath())
                    .into(moviePosterViewHolder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if(mMoviesList != null) {
            return mMoviesList.size();
        }
        return 0;
    }

    /* Adds each movie in JSON from API to a list of Movie objects */
    List<Movie> createMoviesListFromJson(JSONArray moviesArray) {
        List<Movie> moviesList = new ArrayList<>();
        JSONObject currentMovieObj;
        int currentMovieId;
        String currentMoviePosterPath;
        String currentMovieTitle;
        String currentMovieRating;
        String currentMovieReleaseDate;
        String currentMovieOverview;

        for(int i = 0; i < moviesArray.length(); i++) {
            try {
                currentMovieObj = moviesArray.getJSONObject(i);
                currentMovieId = currentMovieObj.getInt(MOVIE_ID_KEY);
                currentMoviePosterPath = "http://image.tmdb.org/t/p/w342" +
                        currentMovieObj.getString(MOVIE_POSTER_PATH_KEY);
                currentMovieTitle = currentMovieObj.getString(MOVIE_TITLE_KEY);
                currentMovieRating = currentMovieObj.getDouble(MOVIE_RATING_KEY) + "/10";
                currentMovieReleaseDate = currentMovieObj.getString(MOVIE_RELEASE_DATE_KEY)
                        .substring(0,4);
                currentMovieOverview = currentMovieObj.getString(MOVIE_OVERVIEW_KEY);

               moviesList.add(new Movie(currentMovieId, currentMoviePosterPath, currentMovieTitle,
                       currentMovieRating, currentMovieReleaseDate, currentMovieOverview));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return moviesList;
    }

    /* Resets movie list and updates UI */
    public void setMovies(List<Movie> movies){
        mMoviesList = movies;
        notifyDataSetChanged();
    }


}
