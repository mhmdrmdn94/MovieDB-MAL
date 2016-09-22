package com.activity_intent.moviedb.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity_intent.moviedb.Adapters.ReviewsAdapter;
import com.activity_intent.moviedb.Adapters.TrailersAdapter;
import com.activity_intent.moviedb.Models.Movie;
import com.activity_intent.moviedb.Networks.FetchReviews;
import com.activity_intent.moviedb.Networks.FetchTrailers;
import com.activity_intent.moviedb.R;
import com.activity_intent.moviedb.SQLite.FavouritsDatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class DetailsFragment  extends android.support.v4.app.Fragment {

    FavouritsDatabaseHelper favouritsDatabaseHelper;
    Button fav;
    private TrailersAdapter mtrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private Movie selectedMovie;

    public DetailsFragment (){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favouritsDatabaseHelper = new FavouritsDatabaseHelper(getActivity());

        setHasOptionsMenu(true);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            selectedMovie = (Movie) savedInstanceState.getSerializable("selected");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("selected", selectedMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // to fetch Trailers and Reviews from server depending on MovieObject passed

        updateTrailers();
        updateReviews();
    }

    private void updateTrailers() {

        FetchTrailers fetchTrailers = new FetchTrailers(mtrailersAdapter);
        fetchTrailers.execute(selectedMovie.getId());   //fetch trailers for selected movie
    }

    private void updateReviews() {

        FetchReviews fetchReviews = new FetchReviews(mReviewsAdapter);
        fetchReviews.execute(selectedMovie.getId());    //fetch reviews for selected movie
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tst, container, false);

            final Movie movie = (Movie)getArguments().getSerializable("selected"); // get passed MovieObject
            selectedMovie = movie;

            getActivity().setTitle(selectedMovie.getOriginalTitle());

            //fit movie details in DetailsUI
            TextView title = (TextView) rootView.findViewById(R.id.detail_title);
            title.setText(movie.getOriginalTitle());

            TextView date = (TextView) rootView.findViewById(R.id.detail_release_year);
            date.setText(movie.getReleaseDate().split("-")[0]);

            TextView rate = (TextView) rootView.findViewById(R.id.detail_rate);
            rate.setText(movie.getUserRate() + " / 10");

            TextView overview = (TextView) rootView.findViewById(R.id.detail_overview);
            overview.setText(movie.getOverview());

            //Asign Error, Temp images for imageView
            ImageView posterImg = (ImageView) rootView.findViewById(R.id.detail_poster_image);
            Picasso.with(getContext()).load(movie.getPosterURL()).placeholder(getContext().getResources().getDrawable(R.drawable.loading))
                    .error(getContext().getResources().getDrawable(R.drawable.no_image)).into(posterImg);

        //TrailersSection

            ListView listView_trailers = (ListView) rootView.findViewById(R.id.trailers);
            mtrailersAdapter = new TrailersAdapter(getContext(),R.layout.trailers_listitem);
            listView_trailers.setAdapter(mtrailersAdapter);

            listView_trailers.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {  // to enable scrolling
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            // to open new intent when clicking on a trailer
            listView_trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Toast.makeText(view.getContext(), mtrailersAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=" + mtrailersAdapter.getItem(position).getKey()));

                    if (intent != null) {

                        if (isIntentAvailable(intent) == true)      // check if there is available app to open trailer
                            startActivity(intent);
                        else
                            Toast.makeText(view.getContext(), "No app available", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            // if no trailers, show a text of "No Trailers" insted of empty list
            TextView emptyT = (TextView) rootView.findViewById(R.id.empty_trailers);
            listView_trailers.setEmptyView(emptyT);


        //Reviews

            ListView listView_reviews = (ListView) rootView.findViewById(R.id.reviews);
            mReviewsAdapter = new ReviewsAdapter(getContext(),R.layout.reviews_listitem);
             listView_reviews.setAdapter(mReviewsAdapter);

            // if no comments, show a text of "No Reviews" instead of empty list
            TextView emptyR = (TextView) rootView.findViewById(R.id.empty_review);
            listView_reviews.setEmptyView(emptyR);


            listView_reviews.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });


        //favourite
            fav = (Button) rootView.findViewById(R.id.detail_favourite_btn);
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String status = favouritsDatabaseHelper.UpdateFav(movie); // update favouriteColumn for specific rowMovie

                    Toast.makeText(v.getContext(),status, Toast.LENGTH_LONG).show(); // show fav. Or UnFav.
                }
            });

        return rootView;
    }

    boolean isIntentAvailable (Intent i ){

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(i,0);
        boolean isIntentSafe = activities.size()>0;
        return isIntentSafe;
    }

}
