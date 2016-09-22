package com.activity_intent.moviedb.Networks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.activity_intent.moviedb.Adapters.ReviewsAdapter;
import com.activity_intent.moviedb.Models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class FetchReviews extends AsyncTask<Integer,Void, ArrayList<Review>>
{

    private  final String LOG_TAG = FetchTrailers.class.getSimpleName();
    private ReviewsAdapter mReviewsAdapter;

    public FetchReviews ( ReviewsAdapter reviewsAdapter){

        this.mReviewsAdapter = reviewsAdapter;
    }

    private ArrayList<Review> getDataFromJson(String fullJSON)
            throws JSONException {

        //parse jsonObject to get TrailersObjects
        JSONObject rootJSON = new JSONObject(fullJSON);
        JSONArray results = rootJSON.getJSONArray("results");
        ArrayList<Review> reviews = new ArrayList<Review>();

        for(int i = 0; i < results.length(); i++)
        {
            JSONObject one_poster = results.getJSONObject(i);
            String author  = one_poster.getString("author");
            String content  = one_poster.getString("content");

            Review review = new Review(author,content);

            reviews.add(review);
        }

        return reviews;
    }


    @Override
    protected ArrayList<Review> doInBackground(Integer... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {

            final String BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"/reviews?";
            final String API_KEY = "{yout_api_key}";

            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter("api_key", API_KEY)
                    .build();

            URL url = new URL(buildUri.toString());
            Log.v(LOG_TAG, "Built URL is " + buildUri.toString());

            // Create the request to MovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();

            Log.v(LOG_TAG,"Reviews JSON String:  " +moviesJsonStr );

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the review data, there's no point in attemping
            // to parse it.
            return null;
        }
        finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e)
                {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        try{

            return  getDataFromJson(moviesJsonStr);

        }catch (JSONException e){
            Log.e(LOG_TAG,e.getMessage(),e);
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {

        if (reviews != null)
        {

          mReviewsAdapter.addAll(reviews); //update Adapter after fetching data
            mReviewsAdapter.notifyDataSetChanged();

        }

    }
}



