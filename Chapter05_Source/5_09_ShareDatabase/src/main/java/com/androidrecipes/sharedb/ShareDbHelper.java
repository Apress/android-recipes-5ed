package com.androidrecipes.sharedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShareDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "frienddb";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "friends";
    public static final String COL_FIRST = "firstName";
    public static final String COL_LAST = "lastName";
    public static final String COL_PHONE = "phoneNumber";

    private static final String STRING_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_FIRST + " TEXT, " + COL_LAST + " TEXT, " + COL_PHONE + " TEXT);";

    public ShareDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create the database table
        db.execSQL(STRING_CREATE);

        //Inserting example values into database
        ContentValues cv = new ContentValues(3);
        cv.put(COL_FIRST, "John");
        cv.put(COL_LAST, "Doe");
        cv.put(COL_PHONE, "8885551234");
        db.insert(TABLE_NAME, null, cv);
        cv = new ContentValues(3);
        cv.put(COL_FIRST, "Jane");
        cv.put(COL_LAST, "Doe");
        cv.put(COL_PHONE, "8885552345");
        db.insert(TABLE_NAME, null, cv);
        cv = new ContentValues(3);
        cv.put(COL_FIRST, "Jill");
        cv.put(COL_LAST, "Doe");
        cv.put(COL_PHONE, "8885553456");
        db.insert(TABLE_NAME, null, cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //For now, clear the database and re-create
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
