package com.activity_intent.moviedb.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.activity_intent.moviedb.Adapters.GridAdapter;
import com.activity_intent.moviedb.Models.Movie;
import com.activity_intent.moviedb.Networks.FetchMovies;
import com.activity_intent.moviedb.Networks.MovieHandler;
import com.activity_intent.moviedb.Networks.onNetworkResponse;
import com.activity_intent.moviedb.R;
import com.activity_intent.moviedb.SQLite.FavouritsDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class MoviesFragment  extends android.support.v4.app.Fragment {

    private  final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private ArrayList<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GridAdapter gridAdapter;
    private View view;

    FavouritsDatabaseHelper favouritsDatabaseHelper;
    private MovieHandler movieHandler;

    private String CURRENT_SORT_TYPE = "popularity.desc"; //default
    private String CURRENT_SORT_KEY = "populars"; //default
    private boolean needFavourites = false;//default


    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favouritsDatabaseHelper = new FavouritsDatabaseHelper(getActivity());

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_top_rated) {
            // check internet connection
            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if ( isConnected == false) {
                Toast.makeText(getContext(), "SORRY, No Internet Connection!", Toast.LENGTH_SHORT).show();
                needFavourites = false; // do not want to get favourites
            }
            else{
                Toast.makeText(getContext(), "Good Internet Connection", Toast.LENGTH_SHORT).show();//networkManager
                CURRENT_SORT_TYPE = "vote_average.desc";
                CURRENT_SORT_KEY = "tops";
                needFavourites = false;

                Log.v(LOG_TAG, "TYPE = "+CURRENT_SORT_TYPE+" / KEY= "+CURRENT_SORT_KEY + " / needFav = "+needFavourites);
                updateMovies(CURRENT_SORT_KEY,CURRENT_SORT_TYPE);   //fetch top rated movies
            }

            return true;

        }
        if (id == R.id.action_populars){

            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if ( isConnected == false) {
                Toast.makeText(getContext(), "SORRY, No Internet Connection!", Toast.LENGTH_SHORT).show();
                needFavourites = false;

            }
            else{
                Toast.makeText(getContext(), "Good Internet Connection", Toast.LENGTH_SHORT).show();//networkManager
                CURRENT_SORT_TYPE = "popularity.desc";
                needFavourites = false;
                CURRENT_SORT_KEY = "populars";
                Log.v(LOG_TAG, "TYPE = "+CURRENT_SORT_TYPE+" / KEY= "+CURRENT_SORT_KEY + " / needFav = "+needFavourites);
                updateMovies(CURRENT_SORT_KEY,CURRENT_SORT_TYPE);
            }

            return true;
        }

        if (id == R.id.action_favourites){

            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if ( isConnected == false) {
                Toast.makeText(getContext(), "SORRY, No Internet Connection!", Toast.LENGTH_SHORT).show();
                CURRENT_SORT_TYPE = "favourites";
                CURRENT_SORT_KEY = "favourites";
                needFavourites = true;
                updateMovies(CURRENT_SORT_KEY, CURRENT_SORT_TYPE);

            }
            else{
                Toast.makeText(getContext(), "Good Internet Connection", Toast.LENGTH_SHORT).show();
                CURRENT_SORT_TYPE = "favourites";
                CURRENT_SORT_KEY = "favourites";
                needFavourites = true;
                Log.v(LOG_TAG, "TYPE = "+CURRENT_SORT_TYPE+" / KEY= "+CURRENT_SORT_KEY + " / needFav = "+needFavourites);
                updateMovies(CURRENT_SORT_KEY,CURRENT_SORT_TYPE);
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();

        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // if no internet connection, pop a toast and empty view
        // if there is a connection, get MostPopular movies

        if ( isConnected == false) {
            Toast.makeText(getContext(), "SORRY, No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Good Internet Connection", Toast.LENGTH_SHORT).show();
            updateMovies(CURRENT_SORT_KEY,CURRENT_SORT_TYPE);
        }
    }


    private void updateMovies(String sortKey,String sortBy) {

        gridAdapter.moviesList.clear();

        FetchMovies fetchMovies = new FetchMovies(getContext(),gridAdapter, movieList, new onNetworkResponse() {
            @Override
            public void onSuccess(ArrayList<Movie> movieArrayList) {


                for (int i=0; i<movieArrayList.size();i++)
                    Log.v(LOG_TAG, "Fav #"+i+"  " + movieArrayList.get(i).getOriginalTitle());

                if (movieArrayList.size( )==0)
                     Log.v(LOG_TAG, "/////////Empty List");


                //when fetching new data, store new ones in my SQLite-DB
                // may be will need  them later when i get to offline mode

                for (int i=0;i<movieArrayList.size();i++){
                    favouritsDatabaseHelper.insertData(movieArrayList.get(i)); // data insertion
                }

                Log.v(LOG_TAG, "?#@#@?      DATABASE UPDATED");


                gridAdapter = new GridAdapter(movieArrayList,getContext(),movieHandler); //refreshing my adapter
                recyclerView.setAdapter(gridAdapter);

                // that is in case of no favourits founded in both "online and offlne modes"
                // this shows a "no favourited movies" TextView
                TextView emptyg = (TextView) view.findViewById(R.id.empty_grid);

                if(needFavourites){ //in case of favourites

                    if (movieArrayList.size() == 0){    //: show the TV and hide the Grid
                        recyclerView.setVisibility(View.GONE);
                        emptyg.setVisibility(View.VISIBLE);
                    }
                    else{       //show the filled grid and hide the TV
                        emptyg.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                else {  //in case of others : show the EMPTY grid and hide the TV followd by NoConnection toast

                        emptyg.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Exception e) {
                Log.e(LOG_TAG, "Error !", e);
            }
        });

        // call the AsyncTask class to fetch data
        // KEY :: for DB or Online fetch
        // VALUE :: for popularity or tops
        fetchMovies.execute(sortKey, sortBy);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sort_by",CURRENT_SORT_TYPE);
        outState.putString("sort_key",CURRENT_SORT_KEY);
        outState.putBoolean("need_fav",needFavourites);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null){
            CURRENT_SORT_TYPE = savedInstanceState.getString("sort_by");
            CURRENT_SORT_KEY = savedInstanceState.getString("sort_key");
            needFavourites = savedInstanceState.getBoolean("need_fav");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        view = rootView;

            recyclerView = (RecyclerView) rootView.findViewById(R.id.gridview);
            Bundle b = this.getArguments();
            layoutManager =  new GridLayoutManager(getContext(),b.getInt("num_of_view_cols")); // to looks good in different screen sizes

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            gridAdapter = new GridAdapter(movieList,getContext(),movieHandler);
            gridAdapter.setHasStableIds(true);

            recyclerView.setAdapter(gridAdapter);


        return rootView;
    }

    //for the selectedMovie
    public void setMovieHandler (MovieHandler movieHandler1){
        movieHandler = movieHandler1;
    }

}
