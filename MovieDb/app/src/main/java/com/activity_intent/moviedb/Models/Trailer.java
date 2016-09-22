package com.activity_intent.moviedb.Models;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class Trailer {

    String Key, Name;

    public Trailer(String key, String name) {
        Key = key;
        Name = name;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
