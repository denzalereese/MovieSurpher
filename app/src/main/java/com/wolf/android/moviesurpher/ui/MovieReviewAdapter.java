package com.wolf.android.moviesurpher.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolf.android.moviesurpher.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MovieReviewAdapter
        extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private LayoutInflater mInflator;
    private List<String> mReviewsList;

    public MovieReviewAdapter(Context context, String reviewJSON) {
        this.mInflator = LayoutInflater.from(context);
        this.mReviewsList = getReviewsListFromJSON(reviewJSON);
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewTextView;

        public MovieReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewTextView = itemView.findViewById(R.id.review_text_view);
        }
    }

    @NonNull
    @Override
    public MovieReviewAdapter.MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                                       int i) {
        View itemView = mInflator.inflate(R.layout.movie_review_item, viewGroup, false);
        return new MovieReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MovieReviewAdapter.MovieReviewViewHolder movieReviewViewHolder, int i) {
        String currentReview = mReviewsList.get(i);
        //load review into ViewHolder's TextView
        movieReviewViewHolder.reviewTextView.setText(currentReview);
    }

    @Override
    public int getItemCount() {
        return mReviewsList.size();
    }

    /* Adds each review's content in JSON from API to a list of Strings */
    private List<String> getReviewsListFromJSON(String data) {
        List<String> movieReviews = new ArrayList<String>();
        try {
            JSONObject dataJSON = new JSONObject(data);
            JSONArray resultsArray = dataJSON.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentReview = resultsArray.getJSONObject(i);
                String currentReviewContent = currentReview.getString("content");
                movieReviews.add(currentReviewContent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieReviews;
    }

}
