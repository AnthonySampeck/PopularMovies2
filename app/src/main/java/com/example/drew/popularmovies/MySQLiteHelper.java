package com.example.drew.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MovieDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIE_TABLE = "CREATE TABLE movies ( " +
                "dbid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "year TEXT, "+
                "image TEXT, "+
                "rating TEXT, "+
                "description TEXT, "+
                "id TEXT )";

        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movies");

        this.onCreate(db);
    }


    private static final String TABLE_MOVIES = "movies";

    private static final String KEY_ID = "dbid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_YEAR = "year";
    private static final String KEY_RATING = "rating";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_MOVIEID="id";





    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_YEAR,KEY_RATING,KEY_DESCRIPTION,KEY_IMAGE,KEY_MOVIEID};

    public void addMovie(Movie movie){
        Log.d("addMovie", movie.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_YEAR, movie.getYear());
        values.put(KEY_IMAGE, movie.getImage());
        values.put(KEY_RATING, movie.getRating());
        values.put(KEY_DESCRIPTION, movie.getDesc());
        values.put(KEY_MOVIEID, movie.getMovieID());


        db.insert(TABLE_MOVIES,
                null,
                values);

        db.close();
    }


    public boolean getMovie(String movie){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_MOVIES,
                        COLUMNS,
                        "id=?",
                        new String[] {String.valueOf(movie)},
                        null,
                        null,
                        null,
                        null);

        if (cursor.moveToFirst()){
            Log.d("cursor", cursor.toString());
return true;
       }
        else return false;
    }




    public void deleteMovie(Movie movie) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MOVIES,
                KEY_MOVIEID + " = ?",
                new String[]{String.valueOf(movie.getMovieID()) });

        db.close();

        Log.d("deleteMovie", movie.toString());

    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new LinkedList<Movie>();

        String query = "SELECT  * FROM " + TABLE_MOVIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Movie movie = null;
        if (cursor.moveToFirst()) {
            do {
                movie = new Movie();
                movie.setDbid(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setYear(cursor.getString(2));
                movie.setImage(cursor.getString(3));
                movie.setRating(cursor.getString(4));
                movie.setDesc(cursor.getString(5));
                movie.setMovieID(cursor.getString(6));

                movies.add(movie);
            } while (cursor.moveToNext());
        }

        Log.d("getAllMovies()", movies.toString());

        return movies;
    }
}