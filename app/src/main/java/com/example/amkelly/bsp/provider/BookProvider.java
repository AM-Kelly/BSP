package com.example.amkelly.bsp.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by Adam on 05/03/2018.
 * This is the database helper class -> used for book information
 */

public class BookProvider extends ContentProvider {
    //Database Columns
    public static final String COLUMN_BOOKID = "_bookid";
    public static final String COLUMN_BOOKTITLE = "book_title";
    public static final String COLUMN_BOOKAUTHOR = "book_author";
    public static final String COLUMN_BOOKISBN = "book_isbn";
    public static final String COLUMN_BOOKABSTRACT = "book_abstract";
    public static final String COLUMN_BOOKPRICE = "book_price";


    //Database Related Constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bsp";
    private static final String DATABASE_TABLE = "books";

    //The DB
    SQLiteDatabase db;

    @Override
    public boolean onCreate()
    {
        //Create a connection to the database
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    //A helper class which will know how to create and update the database
    protected static class DatabaseHelper extends SQLiteOpenHelper
    {
        static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" +
                COLUMN_BOOKID + " integer primary key autoincrement, " +
                COLUMN_BOOKTITLE + " text not null, " +
                COLUMN_BOOKAUTHOR + " text not null, " +
                COLUMN_BOOKISBN + " text not null, " +
                COLUMN_BOOKABSTRACT + " text not null, " +
                COLUMN_BOOKPRICE + " text not null);";/**come back to this one change to integer?*/

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            throw new UnsupportedOperationException();
        }
    }

    //Content Provider URL and Authority
    public static final String AUTHORITY = "com.example.amkelly.bsp.provider.BookProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");

    //MIME Types used for listing tasks or looking for a single task
    private static final String BOOKS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.amkelly.bsp.books";
    private static final String BOOK_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.amkelly.bsp.book";

    //URI Matcher
    private static final int LIST_TASK = 0;
    private static final int ITEM_TASK = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    //Build UriMatcher for search suggestions and shortcut refresh queries
    private static UriMatcher buildUriMatcher()
    {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "book", LIST_TASK);
        matcher.addURI(AUTHORITY, "book/#", ITEM_TASK);
        return matcher;
    }

    //The below method is required to query the supported types
    @Override
    public String getType(Uri uri)
    {
        switch (URI_MATCHER.match(uri))
        {
            case LIST_TASK:
                return BOOKS_MIME_TYPE;
            case ITEM_TASK:
                return BOOK_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
    //Insert into DB
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        if(values.containsKey(COLUMN_BOOKID))
            throw new UnsupportedOperationException();

        long id = db.insertOrThrow(DATABASE_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    //Update DB
    @Override
    public int update(Uri uri, ContentValues values, String ignored1, String[] ignored2)
    {
        if (values.containsKey(COLUMN_BOOKID))
            throw new UnsupportedOperationException();

        int count = db.update(
                DATABASE_TABLE,
                values,
                COLUMN_BOOKID + "=?",//Protection against SQLInjection Attacks
                new String[]{Long.toString(ContentUris.parseId(uri))});

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    //Delete From DB
    @Override
    public int delete(Uri uri, String ignored1, String[] ignored2)
    {
        int count = db.delete(
                DATABASE_TABLE,
                COLUMN_BOOKID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
    //Read From DB
    @Override
    public Cursor query(Uri uri, String[] ignored1, String selection, String[] selectionArgs, String sortOrder)
    {
        String[] projection = new String[]{
                COLUMN_BOOKID,
                COLUMN_BOOKTITLE,
                COLUMN_BOOKAUTHOR,
                COLUMN_BOOKISBN,
                COLUMN_BOOKABSTRACT,
                COLUMN_BOOKPRICE};

        Cursor c;
        switch (URI_MATCHER.match(uri))
        {
            case LIST_TASK:
                c = db.query(DATABASE_TABLE,
                        projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case ITEM_TASK:
                c = db.query(DATABASE_TABLE, projection,
                        COLUMN_BOOKID + "=?",
                        new String[]{Long.toString(ContentUris.parseId(uri))},
                        null, null, null, null);
                if (c.getCount() > 0)
                {
                    c.moveToFirst();
                }
                break;
            default:
                throw new IllegalArgumentException("Unkown URI: " + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

}
