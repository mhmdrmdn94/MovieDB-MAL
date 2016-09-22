package com.activity_intent.moviedb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.activity_intent.moviedb.Fragments.DetailsFragment;
import com.activity_intent.moviedb.Fragments.MoviesFragment;
import com.activity_intent.moviedb.Models.Movie;
import com.activity_intent.moviedb.Networks.MovieHandler;

public class MainActivity extends AppCompatActivity implements MovieHandler {

    private boolean mTwoPane;
    private int num_of_view_cols;
    private Movie currentMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout_left = (FrameLayout) findViewById(R.id.container2);

        if (frameLayout_left == null) {
            //one_pane UI
            mTwoPane = false;

        } else {
            //two-pane UI
            mTwoPane = true;

        }

        if (mTwoPane) {
            num_of_view_cols = 3;
        } else {
            num_of_view_cols = 2;
        }

        if (savedInstanceState == null) {

            MoviesFragment moviesFragment = new MoviesFragment();
            moviesFragment.setMovieHandler(this);
            Bundle b = new Bundle();
            b.putInt("num_of_view_cols", num_of_view_cols);
            moviesFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, moviesFragment)
                    .commit();
        } else {
            currentMovie = (Movie) savedInstanceState.getSerializable("selected");

            MoviesFragment moviesFragment = new MoviesFragment();
            moviesFragment.setMovieHandler(this);
            Bundle b = new Bundle();
            b.putInt("num_of_view_cols",num_of_view_cols);
            moviesFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, moviesFragment)
                    .commit();
        }


    }

    @Override
    public void setSelectedMovie(Movie selectedMovie) {
        if (mTwoPane) {

            currentMovie = selectedMovie;


            DetailsFragment detailsFragment = new DetailsFragment();
            Bundle b = new Bundle();
            b.putSerializable("selected", selectedMovie);
            detailsFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container2, detailsFragment)
                    .commit();
        } else {

            currentMovie = selectedMovie;


            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("selected", selectedMovie);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("selected", currentMovie);

        super.onSaveInstanceState(outState);
    }


}