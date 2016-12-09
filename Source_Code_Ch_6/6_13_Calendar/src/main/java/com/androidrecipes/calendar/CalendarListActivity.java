package com.androidrecipes.calendar;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

public class CalendarListActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final int LOADER_LIST = 100;

    SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_LIST, null, this);

        // Display all calendars in a ListView
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, null,
                new String[]{
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME},
                new int[]{
                        android.R.id.text1, android.R.id.text2}, 0);
        setListAdapter(mAdapter);
        // Listen for item selections
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Cursor c = mAdapter.getCursor();
        if (c != null && c.moveToPosition(position)) {
            Intent intent = new Intent(this, CalendarDetailActivity.class);
            // Pass the _ID and TITLE of the selected calendar to the next
            // Activity
            intent.putExtra(Intent.EXTRA_UID, c.getInt(0));
            intent.putExtra(Intent.EXTRA_TITLE, c.getString(1));
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Return all calendars, ordered by name
        String[] projection = new String[]{CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME};

        return new CursorLoader(this, CalendarContract.Calendars.CONTENT_URI,
                projection, null, null,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
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
