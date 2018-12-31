package com.wolf.android.moviesurpher.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.wolf.android.moviesurpher.network.MovieLoader;
import com.wolf.android.moviesurpher.R;
import com.wolf.android.moviesurpher.database.Movie;
import com.wolf.android.moviesurpher.database.MovieViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private RecyclerView mMoviePosterRecyclerView;
    private MoviePosterAdapter mMoviePosterAdapter;
    private String mSortCategory = MovieLoader.POPULAR_MOVIES; //default sort option
    private MovieViewModel mMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get reference to ViewModel, to interact with favorites database
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        //initialize AsyncTaskLoader (makes network request and loads movie data in an async thread
        if(getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
        //starts loader
        getSupportLoaderManager().restartLoader(0, null, this);

        mMoviePosterRecyclerView = findViewById(R.id.movie_poster_recyclerview);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new MovieLoader(this, mSortCategory); //loads popular movie data by default
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            //parse JSON String from API response into a JSON object
            JSONObject dataJSON = new JSONObject(data);
            JSONArray resultsArray = dataJSON.getJSONArray("results");

            mMoviePosterAdapter = new MoviePosterAdapter(this, resultsArray);
            //populate movie grid RecyclerView using custom adapter
            mMoviePosterRecyclerView.setAdapter(mMoviePosterAdapter);
            mMoviePosterRecyclerView.setLayoutManager(
                    new GridLayoutManager(this, 2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu from custom menu resource
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //check id of menu item selected to get sort order
        switch(item.getItemId()) {
            case R.id.fav_menu_button:
                //gets favorites saved in database via ViewModel LiveData
                mMovieViewModel.getAllMovies().observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(@Nullable List<Movie> movies) {
                        mMoviePosterAdapter.setMovies(movies);
                    }
                });
                break;
            case R.id.popular_menu_button:
                //loads popular movie data from API
                mSortCategory = MovieLoader.POPULAR_MOVIES;
                getSupportLoaderManager().restartLoader(0, null, this);
                break;
            case R.id.top_rated_menu_button:
                //loads top rated movie data from API
                mSortCategory = MovieLoader.TOP_RATED;
                getSupportLoaderManager().restartLoader(0, null, this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
