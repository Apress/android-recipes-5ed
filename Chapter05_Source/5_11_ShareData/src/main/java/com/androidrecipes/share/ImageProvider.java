package com.androidrecipes.share;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.examples.share.imageprovider");

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "nameString";
    public static final String COLUMN_IMAGE = "imageUri";
    /* Default projection if none provided */
    private static final String[] DEFAULT_PROJECTION = {
            COLUMN_ID, COLUMN_NAME, COLUMN_IMAGE
    };

    private String[] mNames, mFilenames;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("This ContentProvider is read-only");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("This ContentProvider is read-only");
    }

    @Override
    public boolean onCreate() {
        mNames = new String[]{"John Doe", "Jane Doe", "Jill Doe"};
        mFilenames = new String[]{"logo1.png", "logo2.png", "logo3.png"};
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Return all columns if no projection given
        if (projection == null) {
            projection = DEFAULT_PROJECTION;
        }
        MatrixCursor cursor = new MatrixCursor(projection);

        for (int i = 0; i < mNames.length; i++) {
            //Insert only the columns they requested
            MatrixCursor.RowBuilder builder = cursor.newRow();
            for (String column : projection) {
                if (COLUMN_ID.equals(column)) {
                    //Use the array index as a unique id
                    builder.add(i);
                }
                if (COLUMN_NAME.equals(column)) {
                    builder.add(mNames[i]);
                }
                if (COLUMN_IMAGE.equals(column)) {
                    builder.add(Uri.withAppendedPath(CONTENT_URI, String.valueOf(i)));
                }
            }
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("This ContentProvider is read-only");
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        int requested = Integer.parseInt(uri.getLastPathSegment());
        AssetFileDescriptor afd;
        AssetManager manager = getContext().getAssets();
        //Return the appropriate asset for the requested item
        try {
            switch (requested) {
                case 0:
                case 1:
                case 2:
                    afd = manager.openFd(mFilenames[requested]);
                    break;
                default:
                    afd = manager.openFd(mFilenames[0]);
                    break;
            }
            return afd;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
