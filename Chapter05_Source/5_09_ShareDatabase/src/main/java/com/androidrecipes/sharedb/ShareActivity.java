package com.androidrecipes.sharedb;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ShareActivity extends FragmentActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final int LOADER_LIST = 100;
    SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_LIST, null, this);

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[]{FriendProvider.Columns.FIRST},
                new int[]{android.R.id.text1}, 0);

        ListView list = new ListView(this);
        list.setOnItemClickListener(this);
        list.setAdapter(mAdapter);

        setContentView(list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        Uri uri = Uri.withAppendedPath(FriendProvider.CONTENT_URI, c.getString(0));
        String[] projection = new String[]{FriendProvider.Columns.FIRST,
                FriendProvider.Columns.LAST,
                FriendProvider.Columns.PHONE};
        //Get the full record
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        String message = String.format("%s %s, %s", cursor.getString(0), cursor.getString(1), cursor.getString(2));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        cursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{FriendProvider.Columns._ID,
                FriendProvider.Columns.FIRST};
        return new CursorLoader(this, FriendProvider.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}