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

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "BookDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE books ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "year TEXT, "+
                "image TEXT, "+
                "rating TEXT, "+
                "description TEXT )";

        // create books table
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");

        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */

    // Books table name
    private static final String TABLE_BOOKS = "books";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_YEAR = "year";
    private static final String KEY_RATING = "rating";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";






    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_YEAR,KEY_RATING,KEY_DESCRIPTION,KEY_IMAGE};

    public void addBook(Movie book){
        Log.d("addBook", book.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID,book.getMovieID());
        values.put(KEY_TITLE, book.getTitle()); // get title
        values.put(KEY_YEAR, book.getYear()); // get author
        values.put(KEY_IMAGE, book.getImage());
        values.put(KEY_RATING, book.getRating());
        values.put(KEY_DESCRIPTION, book.getDesc());


        // 3. insert
        db.insert(TABLE_BOOKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }


    // Updating single book


    // Deleting single book
    public void deleteBook(Movie book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_BOOKS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(book.getMovieID()) });

        // 3. close
        db.close();

        Log.d("deleteBook", book.toString());

    }

    // Get All Books
    public List<Movie> getAllBooks() {
        List<Movie> books = new LinkedList<Movie>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BOOKS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Movie book = null;
        if (cursor.moveToFirst()) {
            do {
                book = new Movie();
                book.setMovieID(cursor.getString(0));
                book.setTitle(cursor.getString(1));
                book.setYear(cursor.getString(2));
                book.setImage(cursor.getString(3));
                book.setRating(cursor.getString(4));
                book.setDesc(cursor.getString(5));

                // Add book to books
                books.add(book);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", books.toString());

        // return books
        return books;
    }
}