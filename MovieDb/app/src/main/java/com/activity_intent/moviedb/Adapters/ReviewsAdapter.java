package com.activity_intent.moviedb.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.activity_intent.moviedb.Models.Review;
import com.activity_intent.moviedb.R;
import java.util.ArrayList;

/**
 * Created by Bebetoo on 9/19/2016.
 */
public class ReviewsAdapter extends ArrayAdapter<Review> {

    public ReviewsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ReviewsAdapter(Context context, int resource, ArrayList<Review> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = convertView;
        if (rootView == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            rootView = vi.inflate(R.layout.reviews_listitem, null);
        }

        //fitting reviews in there listView
        Review review = getItem(position);
        if (review != null){

            TextView Author = (TextView) rootView.findViewById(R.id.review_author);
            Author.setText(review.getAuthor()+" said :");       // append "said" to the author to look good
            TextView Content = (TextView) rootView.findViewById(R.id.review_content);
            Content.setText("\" "+review.getContent()+" \"");  // surround review by double qoutes
        }

        return rootView;
    }
}
