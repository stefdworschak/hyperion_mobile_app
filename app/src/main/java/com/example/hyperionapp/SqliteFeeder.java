package com.example.hyperionapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class SqliteFeeder extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "hyperion_local.db";
    File file=new File("hyperion_local.db");

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SqliteHelper.FeedEntry.TABLE_NAME + " (" +
                    SqliteHelper.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    SqliteHelper.FeedEntry.COLUMN_NAME_FULLNAME + " TEXT," +
                    SqliteHelper.FeedEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    SqliteHelper.FeedEntry.COLUMN_NAME_DOB + " DATE," +
                    SqliteHelper.FeedEntry.COLUMN_NAME_ADDRESS + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SqliteHelper.FeedEntry.TABLE_NAME;

    public SqliteFeeder(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}