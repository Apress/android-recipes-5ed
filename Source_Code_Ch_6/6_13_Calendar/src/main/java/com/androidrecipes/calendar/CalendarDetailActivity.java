package com.androidrecipes.calendar;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarDetailActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    private static final int LOADER_DETAIL = 101;

    SimpleCursorAdapter mAdapter;

    int mCalendarId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendarId = getIntent().getIntExtra(Intent.EXTRA_UID, -1);

        String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        setTitle(title);

        getLoaderManager().initLoader(LOADER_DETAIL, null, this);

        // Display all events in a ListView
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, null,
                new String[]{
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION},
                new int[]{
                        android.R.id.text1, android.R.id.text2}, 0);
        setListAdapter(mAdapter);
        // Listen for item selections
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Add Event")
                .setIcon(android.R.drawable.ic_menu_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showAddEventDialog();
        return true;
    }

    // Display a dialog to add a new event
    private void showAddEventDialog() {
        final EditText nameText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Event");
        builder.setView(nameText);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Add Event",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addEvent(nameText.getText().toString());
                    }
                });
        builder.show();
    }

    // Add an event to the calendar with the specified name
    // and the current time as the start date
    private void addEvent(String eventName) {
        long start = System.currentTimeMillis();
        // End 1 hour from now
        long end = start + (3600 * 1000);

        ContentValues cv = new ContentValues(5);
        cv.put(CalendarContract.Events.CALENDAR_ID, mCalendarId);
        cv.put(CalendarContract.Events.TITLE, eventName);
        cv.put(CalendarContract.Events.DESCRIPTION,
                "Event created by Android Recipes");
        cv.put(CalendarContract.Events.EVENT_TIMEZONE,
                Time.getCurrentTimezone());
        cv.put(CalendarContract.Events.DTSTART, start);
        cv.put(CalendarContract.Events.DTEND, end);

        getContentResolver().insert(CalendarContract.Events.CONTENT_URI, cv);
    }

    // Remove the selected event from the calendar
    private void deleteEvent(int eventId) {
        String selection = CalendarContract.Events._ID + " = ?";
        String[] selectionArgs = {String.valueOf(eventId)};
        getContentResolver().delete(CalendarContract.Events.CONTENT_URI,
                selection, selectionArgs);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Cursor c = mAdapter.getCursor();
        if (c != null && c.moveToPosition(position)) {
            // Show a dialog with more detailed data about the event when
            // clicked
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder sb = new StringBuilder();

            sb.append("Location: "
                    + c.getString(
                    c.getColumnIndex(CalendarContract.Events.EVENT_LOCATION))
                    + "\n\n");
            int startDateIndex = c.getColumnIndex(CalendarContract.Events.DTSTART);
            Date startDate = c.isNull(startDateIndex) ? null
                    : new Date(Long.parseLong(c.getString(startDateIndex)));
            if (startDate != null) {
                sb.append("Starts At: " + sdf.format(startDate) + "\n\n");
            }
            int endDateIndex = c.getColumnIndex(CalendarContract.Events.DTEND);
            Date endDate = c.isNull(endDateIndex) ? null
                    : new Date(Long.parseLong(c.getString(endDateIndex)));
            if (endDate != null) {
                sb.append("Ends At: " + sdf.format(endDate) + "\n\n");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(
                    c.getString(c.getColumnIndex(CalendarContract.Events.TITLE)));
            builder.setMessage(sb.toString());
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        Cursor c = mAdapter.getCursor();
        if (c != null && c.moveToPosition(position)) {
            // Allow the user to delete the event on a long-press
            final int eventId = c.getInt(
                    c.getColumnIndex(CalendarContract.Events._ID));
            String eventName = c.getString(
                    c.getColumnIndex(CalendarContract.Events.TITLE));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Event");
            builder.setMessage(String.format(
                    "Are you sure you want to delete %s?",
                    TextUtils.isEmpty(eventName) ? "this event" : eventName));
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Delete Event",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteEvent(eventId);
                        }
                    });
            builder.show();
        }

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Return all calendars, ordered by name
        String[] projection = new String[]{CalendarContract.Events._ID,
                CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_LOCATION};
        String selection = CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = {String.valueOf(mCalendarId)};

        return new CursorLoader(this, CalendarContract.Events.CONTENT_URI,
                projection, selection, selectionArgs,
                CalendarContract.Events.DTSTART + " DESC");
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
