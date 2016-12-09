package com.androidrecipes.appwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

public class MediaService extends Service {

    private ContentObserver mMediaStoreObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        //Create a register a new observer on the MediaStore when this Service begins
        mMediaStoreObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                //Update all the widgets currently attached to our AppWidgetProvider
                AppWidgetManager manager = AppWidgetManager.getInstance(MediaService.this);
                ComponentName provider = new ComponentName(MediaService.this, ListAppWidget.class);
                int[] appWidgetIds = manager.getAppWidgetIds(provider);
                //This method triggers onDataSetChanged() in the RemoteViewsService
                manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list);
            }
        };
        //Register for Images and Video
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mMediaStoreObserver);
        getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, mMediaStoreObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister the observer when the Service stops
        getContentResolver().unregisterContentObserver(mMediaStoreObserver);
    }

    /*
     * We are not binding to this Service, so this method should
     * just return null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
