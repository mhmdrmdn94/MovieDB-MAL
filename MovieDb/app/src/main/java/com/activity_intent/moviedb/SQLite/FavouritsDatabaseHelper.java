package com.activity_intent.moviedb.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.activity_intent.moviedb.Models.Movie;

import java.util.ArrayList;

/**
 * Created by Bebetoo on 9/15/2016.
 */
public class FavouritsDatabaseHelper  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MovieDB";
    public static final String TABLE_NAME = "Latest";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "Name";
    public static final String COL_3 = "Poster";
    public static final String COL_4 = "Overview";
    public static final String COL_5 = "ReleaseDate";
    public static final String COL_6 = "Rate";
    public static final String COL_7= "Flag";

    public FavouritsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY , " + COL_2 + " TEXT," + COL_3 +
                " TEXT, " + COL_4 + " TEXT, " + COL_5 + " TEXT, " + COL_6 + " TEXT, " + COL_7 + " INTEGER DEFAULT 0 " + ");");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public String insertData ( Movie movie){

        // check first if the movie already exists in db
        // if not, then insert it in db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = ?", new String[]{movie.getId() + ""});

        if (cursor.getCount() == 0){

            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1,movie.getId());
            contentValues.put(COL_2,movie.getOriginalTitle());
            contentValues.put(COL_3,movie.getPosterURL());
            contentValues.put(COL_4,movie.getOverview());
            contentValues.put(COL_5,movie.getReleaseDate());
            contentValues.put(COL_6,movie.getUserRate());
            contentValues.put(COL_7,movie.getFlag());

            long result = db.insert(TABLE_NAME,null,contentValues);

            if (result == -1)
                return "ERROR!!!";
            else
                return "Inserted";
        }
        else
            return "Exists";


    }


    public ArrayList<Movie> getDATA (){

        //to retrive all movies stored in db
        // this may help in case of OFFLINE mode "FutureWork"

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<Movie> movieArrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(cursor.getInt(0));
                movie.setOriginalTitle(cursor.getString(1));
                movie.setOverview(cursor.getString(3));
                movie.setPosterURL(cursor.getString(2));
                movie.setReleaseDate(cursor.getString(4));
                movie.setUserRate(Double.parseDouble(cursor.getString(5)));
                movie.setFlag(Integer.parseInt(cursor.getString(6)));

                movieArrayList.add(movie);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return movieArrayList;

    }

    public ArrayList<Movie> getFavourites (){

        //to retrieve only FAVOURITED movies which their FLAG = 1

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+COL_7+" = 1", null);

        ArrayList<Movie> movieArrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(cursor.getInt(0));
                movie.setOriginalTitle(cursor.getString(1));
                movie.setOverview(cursor.getString(3));
                movie.setPosterURL(cursor.getString(2));
                movie.setReleaseDate(cursor.getString(4));
                movie.setUserRate(Double.parseDouble(cursor.getString(5)));
                movie.setFlag(Integer.parseInt(cursor.getString(6)));

                movieArrayList.add(movie);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return movieArrayList;

    }


    public String UpdateFav (Movie movie){

        //to favourite and Un-favourite movies, which commands are comming from Fav / unfav Button

        String status = "";

        // 1. get the specific movie

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = ?", new String[]{movie.getId() + ""});

        // 2. update status "flip the flag"

        cursor.moveToFirst();
        int newFlag = cursor.getInt(6);
        if (newFlag == 0)
            newFlag=1;
        else    newFlag=0;

        ContentValues cv = new ContentValues();
        cv.put(COL_7, newFlag);
        db.update(TABLE_NAME, cv, COL_1 + "= ?", new String[] {movie.getId()+""});


        status = (newFlag == 1) ? "Favourited":"Un-Favourited";  // for TOASTs

        return status;
    }

}
