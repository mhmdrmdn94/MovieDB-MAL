package com.activity_intent.moviedb.Models;

import java.io.Serializable;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class Movie implements Serializable {

    //I have made Movie to implement SERIALIZABLE to be able to sent via "Intents"

    String poster_URL;
    String originalTitle;
    String overview;
    int id;
    int flag =0; //for favourites
    String releaseDate;
    double userRate;


    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public double getUserRate() {
        return userRate;
    }

    public void setUserRate(double userRate) {
        this.userRate = userRate;
    }

    public String getPosterURL() {
        return poster_URL;
    }

    public void setPosterURL(String posterURL) {
        this.poster_URL = posterURL;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}

