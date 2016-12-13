package com.androidrecipes.sharedb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FriendProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.examples.sharedb.friendprovider/friends");

    public static final class Columns {
        public static final String _ID = "_id";
        public static final String FIRST = "firstName";
        public static final String LAST = "lastName";
        public static final String PHONE = "phoneNumber";
    }

    /* Uri Matching */
    private static final int FRIEND = 1;
    private static final int FRIEND_ID = 2;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(CONTENT_URI.getAuthority(), "friends", FRIEND);
        matcher.addURI(CONTENT_URI.getAuthority(), "friends/#", FRIEND_ID);
    }

    SQLiteDatabase db;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result = matcher.match(uri);
        switch (result) {
            case FRIEND:
                return db.delete(ShareDbHelper.TABLE_NAME, selection, selectionArgs);
            case FRIEND_ID:
                return db.delete(ShareDbHelper.TABLE_NAME, "_ID = ?", new String[]{uri.getLastPathSegment()});
            default:
                return 0;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert(ShareDbHelper.TABLE_NAME, null, values);
        if (id >= 0) {
            return Uri.withAppendedPath(uri, String.valueOf(id));
        } else {
            return null;
        }
    }

    @Override
    public boolean onCreate() {
        ShareDbHelper helper = new ShareDbHelper(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int result = matcher.match(uri);
        switch (result) {
            case FRIEND:
                return db.query(ShareDbHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            case FRIEND_ID:
                return db.query(ShareDbHelper.TABLE_NAME, projection, "_ID = ?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result = matcher.match(uri);
        switch (result) {
            case FRIEND:
                return db.update(ShareDbHelper.TABLE_NAME, values, selection, selectionArgs);
            case FRIEND_ID:
                return db.update(ShareDbHelper.TABLE_NAME, values, "_ID = ?", new String[]{uri.getLastPathSegment()});
            default:
                return 0;
        }
    }

}
