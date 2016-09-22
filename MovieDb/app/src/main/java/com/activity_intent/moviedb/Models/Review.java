package com.activity_intent.moviedb.Models;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class Review {
    String Author, Content;

    public Review(String author, String content) {
        Author = author;
        Content = content;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
