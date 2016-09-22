package com.activity_intent.moviedb.Networks;

import com.activity_intent.moviedb.Models.Movie;

import java.util.ArrayList;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public interface onNetworkResponse {

    public void onSuccess(ArrayList<Movie> movieArrayList);
    public void onFailure(Exception e);

}
