package com.activity_intent.moviedb.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.activity_intent.moviedb.Models.Movie;
import com.activity_intent.moviedb.Networks.MovieHandler;
import com.activity_intent.moviedb.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder>  {

    private  final String LOG_TAG = GridAdapter.class.getSimpleName();

    public  List<Movie> moviesList;
    private Context mcontext;
    private RecyclerView recyclerView;
    public MovieHandler movieHandler;


    public GridAdapter(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }
    public GridAdapter(List<Movie> moviesList,Context mcontext,MovieHandler movieHandler) {
        this.moviesList = moviesList;
        this.mcontext = mcontext;
        this.movieHandler=movieHandler;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder,  int position) {

        final Movie movie = moviesList.get(position);
        //Assign new, temp, error image
        Picasso.with(mcontext).load(movie.getPosterURL()).placeholder(mcontext.getResources().getDrawable(R.drawable.loading))
                .error(mcontext.getResources().getDrawable(R.drawable.no_image)).into(holder.tv_poster_image);

        //On posterClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                movieHandler.setSelectedMovie(movie);
            }
        });

        Log.v(LOG_TAG, "Movie List SIZE =="+ moviesList.size());

        for (int i=0;i<moviesList.size(); i++)                  //to view adapter contents
            Log.v(LOG_TAG, "Movie #" + i + moviesList.get(i).getOriginalTitle());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    //in case of adapter needed to be refreshed from background
    public void update (ArrayList<Movie> arrayList){

        moviesList.clear();
        moviesList = arrayList;
        super.notifyDataSetChanged();

    }

    public void setMovieHandler (MovieHandler movieHandler1){
        movieHandler = movieHandler1;
    } //to tell others about selectedMovie details


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView tv_poster_image;
        public MyViewHolder(View view) {
            super(view);

            tv_poster_image = (ImageView) view.findViewById(R.id.imgview_poster);

        }
    }


}


