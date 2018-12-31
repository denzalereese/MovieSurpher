package com.wolf.android.moviesurpher.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;
import com.wolf.android.moviesurpher.network.MovieLoader;
import com.wolf.android.moviesurpher.R;
import com.wolf.android.moviesurpher.database.Movie;
import com.wolf.android.moviesurpher.database.MovieViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    private int mMovieId;
    private String mMovieTitle;
    private String mMoviePosterPath;
    private String mMovieReleaseDate;
    private String mMovieRating;
    private String mMovieOverview;

    private MaterialFavoriteButton mFavoriteButton;
    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterImageView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieOverviewTextView;
    private RecyclerView mReviewsRecyclerView;
    private ListView mTrailersListView;

    private MovieViewModel mMovieViewModel;

    //use different id's to differentiate between trailer and review Loaders
    private static int TRAILER_LOADER_ID = 0;
    private static int REVIEW_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get references to XML Views as Java objects
        mFavoriteButton = findViewById(R.id.favorite_button);
        mMovieTitleTextView = findViewById(R.id.movie_title);
        mMoviePosterImageView = findViewById(R.id.movie_poster);
        mMovieReleaseDateTextView = findViewById(R.id.movie_release_date);
        mMovieRatingTextView = findViewById(R.id.movie_rating);
        mMovieOverviewTextView = findViewById(R.id.movie_overview);

        //get details data from intent bundle and populate appropriate Views
        bindDetailsDataToViews();

        //get references to XML reviews & trailer lists as Java objects
        mReviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        mTrailersListView = findViewById(R.id.trailers_list_view);

        //initialize & start Loaders to load reviews and trailers from API
        startTrailerAndReviewLoaders();

        //get reference to ViewModel so that movie can be saved as favorite to database
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        //save movie to database when favorited
        mFavoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                saveFavorite();
            }
        });
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle bundle) {
        //load reviews and trailers for the movie
        if(id == TRAILER_LOADER_ID) {
            //if loader ID is for trailers, load trailers from API
            return new MovieLoader(getApplicationContext(), MovieLoader.MOVIE_TRAILERS, mMovieId);
        }
        else if(id == REVIEW_LOADER_ID) {
            //if loader ID is for reviews, load reviews from API
            return new MovieLoader(getApplicationContext(), MovieLoader.MOVIE_REVIEWS, mMovieId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        //call appropriate helper method after loader finishes fetching data from API
        if(loader.getId() == TRAILER_LOADER_ID) {
            handleTrailerLoadFinish(data);
        }
        else if (loader.getId() == REVIEW_LOADER_ID) {
            handleReviewLoadFinish(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {}

    private void bindDetailsDataToViews() {
        //get reference to Intent from MainActivity that opened this DetailActivity
        Intent intentFromMainActivity = getIntent();

        //store details data from the Intent Bundle about which movie was clicked
        Bundle clickedMovieDetailsBundle = intentFromMainActivity.getExtras();
        mMovieId =
                clickedMovieDetailsBundle.getInt(MoviePosterAdapter.MOVIE_ID_KEY);
        mMovieTitle =
                clickedMovieDetailsBundle.getString(MoviePosterAdapter.MOVIE_TITLE_KEY);
        mMoviePosterPath =
                clickedMovieDetailsBundle.getString(MoviePosterAdapter.MOVIE_POSTER_PATH_KEY);
        mMovieReleaseDate =
                clickedMovieDetailsBundle.getString(MoviePosterAdapter.MOVIE_RELEASE_DATE_KEY);
        mMovieRating =
                clickedMovieDetailsBundle.getString(MoviePosterAdapter.MOVIE_RATING_KEY);
        mMovieOverview =
                clickedMovieDetailsBundle.getString(MoviePosterAdapter.MOVIE_OVERVIEW_KEY);

        //populate TextViews and ImageViews with details data
        mMovieTitleTextView.setText(mMovieTitle);
        //use Picasso library to load images for better memory management/automatic caching
        Picasso.with(this).load(mMoviePosterPath).into(mMoviePosterImageView);
        mMovieReleaseDateTextView.setText(mMovieReleaseDate);
        mMovieRatingTextView.setText(mMovieRating);
        mMovieOverviewTextView.setText(mMovieOverview);
    }

    /* Initializes and starts loaders to load trailer and review data from API */
    private void startTrailerAndReviewLoaders() {
        if(getSupportLoaderManager().getLoader(TRAILER_LOADER_ID) != null) {
            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID,
                    null, this);
        }

        getSupportLoaderManager().restartLoader(TRAILER_LOADER_ID,
                null, this);

        if(getSupportLoaderManager().getLoader(REVIEW_LOADER_ID) != null) {
            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID,
                    null, this);
        }

        getSupportLoaderManager().restartLoader(REVIEW_LOADER_ID,
                null, this);
    }


    /*  Gets trailer JSON fom API and adds each trailer (YouTube key) String to a list.
     * This list is used to populate the trailer ListView. */
    private void handleTrailerLoadFinish(String data) {
        final List<String> movieTrailers = new ArrayList<String>();

        String YOUTUBE_PATH = "https://www.youtube.com/watch?v=";
        String YOUTUBE_KEY = "key";

        try {
            //parse JSON string from API to JSONObject
            JSONObject dataJSON = new JSONObject(data);
            //array of trailer objects from API
            JSONArray resultsArray = dataJSON.getJSONArray("results");

            //iterate through each trailer object
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentTrailer = resultsArray.getJSONObject(i);
                //create full YouTube url using the base path and the Youtube key from the object
                String trailerUrl = YOUTUBE_PATH + currentTrailer.getString(YOUTUBE_KEY);
                movieTrailers.add(trailerUrl);
            }

            //populate trailer ListView with trailer TextViews
            mTrailersListView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, movieTrailers) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView,
                                    @NonNull ViewGroup parent) {
                    TextView trailerTextView =
                            (TextView) super.getView(position, convertView, parent);
                    trailerTextView.setText("Trailer #" + (position + 1));
                    trailerTextView.setTextColor(Color.WHITE);
                    trailerTextView.setPadding(0, 0, 0, 0);
                    return trailerTextView;
                }
            });

            //When clicked, opens the YouTube app to the trailer at this YouTube url
            mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Uri trailerUrlClicked = Uri.parse(movieTrailers.get(i));
                    Intent intentToYouTube = new Intent(Intent.ACTION_VIEW, trailerUrlClicked);
                    startActivity(intentToYouTube);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Gets review JSON from API and adds each review String to a list.
    * This list is used to populate the reviews RecyclerView. */
    private void handleReviewLoadFinish(String data) {
        List<String> movieReviews = new ArrayList<String>();

        try {
            //parse JSON string from API to JSONObject
            JSONObject dataJSON = new JSONObject(data);
            //array of review objects from API
            JSONArray resultsArray = dataJSON.getJSONArray("results");

            //iterate through each review object
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentReview = resultsArray.getJSONObject(i);
                //get review content from object
                String currentReviewContent = currentReview.getString("content");
                movieReviews.add(currentReviewContent);
            }

            //populate RecyclerView with reviews
            MovieReviewAdapter reviewAdapter = new MovieReviewAdapter(this, data);
            mReviewsRecyclerView.setAdapter(reviewAdapter);
            mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Saves movie to local Room database via ViewModel */
    public void saveFavorite() {
       mMovieViewModel.insert(new Movie(mMovieId, mMoviePosterPath, mMovieTitle,
               mMovieRating, mMovieReleaseDate, mMovieOverview));
    }
}
