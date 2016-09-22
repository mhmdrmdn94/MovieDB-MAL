package com.activity_intent.moviedb.Networks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.activity_intent.moviedb.Adapters.GridAdapter;
import com.activity_intent.moviedb.Models.Movie;
import com.activity_intent.moviedb.SQLite.FavouritsDatabaseHelper;

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
public class FetchMovies extends AsyncTask<String,Void,ArrayList<Movie>>
{

    private  final String LOG_TAG = FetchMovies.class.getSimpleName();
    private ArrayList<Movie> moviesList = new ArrayList<>();
    private GridAdapter gridAdapter;
    private Context mContext;

     onNetworkResponse onNetwork_Response;
     Exception exception;

    private ImageView imageView ;       // Future work
    private ProgressBar progressBar;        // Future work

    public FetchMovies (Context context, GridAdapter gridAdapter, ArrayList<Movie> movieList,onNetworkResponse onNetwork_Response){
        this.moviesList = movieList;
        this.mContext = context;        //for accessing DB
        this.gridAdapter = gridAdapter;
        this.onNetwork_Response = onNetwork_Response;
    }

    private ArrayList<Movie> getDataFromJson(String fullJSON)
            throws JSONException {

        //trying to get every movie detail into a single movie object

        JSONObject rootJSON = new JSONObject(fullJSON);
        JSONArray results = rootJSON.getJSONArray("results");
        for(int i = 0; i < results.length(); i++)
        {
            JSONObject one_film = results.getJSONObject(i);
            String posterPath  = "http://image.tmdb.org/t/p/w185/"+one_film.getString("poster_path");
            String originalTitle = one_film.getString("original_title");
            String overview = one_film.getString("overview");
            int id = one_film.getInt("id");
            String releaseDate = one_film.getString("release_date");
            double userRate = one_film.getDouble("vote_average");

            Movie movie = new Movie();

            movie.setPosterURL(posterPath);
            movie.setOriginalTitle(originalTitle);
            movie.setOverview(overview);
            movie.setId(id);
            movie.setReleaseDate(releaseDate);
            movie.setUserRate(userRate);

            moviesList.add(movie);  // add new movies to the list

            Log.v(LOG_TAG, "Movie #" + i + movie.getOriginalTitle());

        }

        return moviesList;
    }


    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        String KEY = params[0]; // fetch from Db or server?
        String VALUE = params[1];   // from server? populars : tops ?



        if (KEY.equalsIgnoreCase("favourites")) {

            FavouritsDatabaseHelper favouritsDatabaseHelper = new FavouritsDatabaseHelper(mContext);
            ArrayList<Movie> movieArrayList = favouritsDatabaseHelper.getFavourites();

            for (int i=0; i<movieArrayList.size();i++)
                Log.v(LOG_TAG, "Fav #"+i+"  " + movieArrayList.get(i).getOriginalTitle());

            moviesList = movieArrayList;
            return moviesList;

        }
        else {
                    // fetch from server
            try {

                final String BASE_URL = "http://api.themoviedb.org/3/";
                final String DISCOVER_URL = "discover/movie?";
                final String API_KEY = "83268b175c6cdb760409eead3c9ec72a";

                Uri buildUri = Uri.parse(BASE_URL + DISCOVER_URL).buildUpon()
                        //  .appendQueryParameter("sort_by", "popularity.desc")
                        .appendQueryParameter("sort_by", params[1])
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

                moviesJsonStr = buffer.toString(); // get jsonObj in a string from  the buffer
                Log.v(LOG_TAG, "Movie JSON String:  " + moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data,
                // there's no point in attemping
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

                return  getDataFromJson(moviesJsonStr); //parse the jasonObj and retrieve movie objects

            }catch (JSONException e){
                exception = e;
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }//end of server work

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Future work
        //Spinners for posters
        //shoof DELTA course
        //lesson 13

        // how to access VIEWs inside AsyncTsk ?? innerClass only

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        // Future work

        ///Spinners for posters
        //shoof DELTA course
        //lesson 13


        // how to access VIEWs inside AsyncTsk ?? innerClass only

    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {

//        if (movies != null)
//        {
//            gridAdapter.update(movies);
//
//        }


        if (onNetwork_Response != null){
            onNetwork_Response.onSuccess(moviesList);
        }
        else{
            onNetwork_Response.onFailure(exception);
        }

    }
}

