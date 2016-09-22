package com.activity_intent.moviedb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activity_intent.moviedb.Models.Movie;
import com.activity_intent.moviedb.Models.Trailer;
import com.activity_intent.moviedb.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bebetoo on 9/7/2016.
 */
public class TrailersAdapter extends ArrayAdapter<Trailer> {

    public TrailersAdapter(Context context, int resource) {
        super(context, resource);
    }

    public TrailersAdapter(Context context, int resource, ArrayList<Trailer> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = convertView;
        if (rootView == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            rootView = vi.inflate(R.layout.trailers_listitem, null);
        }

        //fitting each trailer in the listView
        Trailer trailer = getItem(position);
        if (trailer != null) {
            TextView textView = (TextView) rootView.findViewById(R.id.trailer_num);
            textView.setText(trailer.getName());
        }
        return rootView;
    }



}
