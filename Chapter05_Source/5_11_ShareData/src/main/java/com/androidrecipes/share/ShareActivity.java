package com.androidrecipes.share;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ShareActivity extends FragmentActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final int LOADER_LIST = 100;
    SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_LIST, null, this);
        setContentView(R.layout.main);

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                null, new String[]{ImageProvider.COLUMN_NAME}, new int[]{android.R.id.text1}, 0);

        ListView list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(this);
        list.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //Seek the cursor to the selection
        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);

        //Load the name column into the TextView
        TextView tv = (TextView) findViewById(R.id.name);
        tv.setText(c.getString(1));

        ImageView iv = (ImageView) findViewById(R.id.image);
        try {
            //Load the content from the image column into the ImageView
            InputStream in = getContentResolver().openInputStream(Uri.parse(c.getString(2)));
            Bitmap image = BitmapFactory.decodeStream(in);
            iv.setImageBitmap(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{"_id",
                ImageProvider.COLUMN_NAME,
                ImageProvider.COLUMN_IMAGE};
        return new CursorLoader(this, ImageProvider.CONTENT_URI,
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