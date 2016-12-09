package com.androidrecipes.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileInputStream;

public class DownloadActivity extends Activity {

    private static final String DL_ID = "downloadId";
    private SharedPreferences prefs;
    private DownloadManager dm;
    private ImageView imageView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        setContentView(imageView);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!prefs.contains(DL_ID)) {
            //Start the download
            Uri resource = Uri.parse("http://www.bigfoto.com/dog-animal.jpg");
            DownloadManager.Request request = new DownloadManager.Request(resource);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                    | DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false);
            //Display in the notification bar
            request.setTitle("Download Sample");
            long id = dm.enqueue(request);
            //Save the unique id
            prefs.edit().putLong(DL_ID, id).commit();
        } else {
            //Download already started, check status
            queryDownloadStatus();
        }

        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            queryDownloadStatus();
        }
    };

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(prefs.getLong(DL_ID, 0));
        Cursor c = dm.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            Log.d("DM Sample", "Status Check: " + status);
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_RUNNING:
                    //Do nothing, still in progress
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //Done, display the image
                    try {
                        ParcelFileDescriptor file = dm.openDownloadedFile(prefs.getLong(DL_ID, 0));
                        FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(file);
                        imageView.setImageBitmap(BitmapFactory.decodeStream(fis));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    //Clear the download and try again later
                    dm.remove(prefs.getLong(DL_ID, 0));
                    prefs.edit().clear().commit();
                    break;
            }
        }
    }
}