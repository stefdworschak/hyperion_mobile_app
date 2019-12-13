package com.example.hyperionapp;

import android.provider.BaseColumns;
/*

    Reference: https://developer.android.com/training/data-storage/sqlite

 */
public final class SqliteHelper {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SqliteHelper() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "hyperion_local";
        public static final String COLUMN_NAME_FULLNAME = "fullname";
        public static final String COLUMN_NAME_EMAIL = "email_address";
        public static final String COLUMN_NAME_DOB = "dob";
        public static final String COLUMN_NAME_ADDRESS = "address";
    }

}