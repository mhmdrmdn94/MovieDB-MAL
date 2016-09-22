package com.activity_intent.moviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.activity_intent.moviedb.Fragments.DetailsFragment;
import com.activity_intent.moviedb.Fragments.MoviesFragment;
import com.activity_intent.moviedb.Models.Movie;
import com.activity_intent.moviedb.SQLite.FavouritsDatabaseHelper;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Movie movie = (Movie) getIntent().getSerializableExtra("selected");
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected", movie);

        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setArguments(bundle);


        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container2, detailsFragment)
                    .commit();
        }


    }
}

