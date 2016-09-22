package com.activity_intent.moviedb.Networks;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.activity_intent.moviedb.Adapters.GridAdapter;
import com.activity_intent.moviedb.Adapters.TrailersAdapter;
import com.activity_intent.moviedb.Models.Movie;
import com.activity_intent.moviedb.Models.Trailer;

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
public class FetchTrailers extends AsyncTask<Integer,Void, ArrayList<Trailer>>
{

    private  final String LOG_TAG = FetchTrailers.class.getSimpleName();
    private TrailersAdapter trailersAdapter;

    public FetchTrailers ( TrailersAdapter trailersAdapter){
        this.trailersAdapter = trailersAdapter;
    }

    private ArrayList<Trailer> getDataFromJson(String fullJSON)
            throws JSONException {

        //extract each trailer from json

        JSONObject rootJSON = new JSONObject(fullJSON);
        JSONArray results = rootJSON.getJSONArray("results");

        ArrayList<Trailer> trailers = new ArrayList<Trailer>();

        for(int i = 0; i < results.length(); i++)
        {
            JSONObject one_poster = results.getJSONObject(i);
            String name  = one_poster.getString("name");
            String key  = one_poster.getString("key");

            Trailer trailer = new Trailer(key,name);

            trailers.add(trailer);
        }


        return trailers;
    }


    @Override
    protected ArrayList<Trailer> doInBackground(Integer... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {

            final String BASE_URL = "http://api.themoviedb.org/3/movie/"+params[0]+"/videos?append_to_response=videos";
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

            Log.v(LOG_TAG,"Trailers JSON String:  " +moviesJsonStr );

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the trailer data, there's no point in attemping
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
    protected void onPostExecute(ArrayList<Trailer> trailers) {

        if(trailers != null){
            trailersAdapter.addAll(trailers);//update Adapter after fetching data
            trailersAdapter.notifyDataSetChanged();

        }

    }
}


