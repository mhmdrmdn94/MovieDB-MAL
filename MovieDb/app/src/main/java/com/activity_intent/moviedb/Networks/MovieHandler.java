package com.activity_intent.moviedb.Networks;

import com.activity_intent.moviedb.Models.Movie;

/**
 * Created by Bebetoo on 9/19/2016.
 */
public interface MovieHandler { //used to announce that Movie X is been selected
    public void setSelectedMovie (Movie selectedMovie);
}
