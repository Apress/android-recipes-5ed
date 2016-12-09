package com.androidrecipes.appwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class RandomService extends Service {
    /* Broadcast Action When Updates Complete */
    public static final String ACTION_RANDOM_NUMBER = "com.examples.appwidget.ACTION_RANDOM_NUMBER";

    /* Current Data Saved as a static value */
    private static int sRandomNumber;

    public static int getRandomNumber() {
        return sRandomNumber;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Update the random number data
        sRandomNumber = (int) (Math.random() * 100);

        //Create the AppWidget view
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.simple_widget_layout);
        views.setTextViewText(R.id.text_number, String.valueOf(sRandomNumber));

        //Set an Intent for the refresh button to start this service again
        PendingIntent refreshIntent = PendingIntent.getService(this, 0,
                new Intent(this, RandomService.class), 0);
        views.setOnClickPendingIntent(R.id.button_refresh, refreshIntent);

        //Set an Intent so tapping the widget text will open the Activity
        PendingIntent appIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        views.setOnClickPendingIntent(R.id.container, appIntent);

        //Update the widget
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName widget = new ComponentName(this, SimpleAppWidget.class);
        manager.updateAppWidget(widget, views);

        //Fire a broadcast to notify listeners
        Intent broadcast = new Intent(ACTION_RANDOM_NUMBER);
        sendBroadcast(broadcast);

        //This service should not continue to run
        stopSelf();
        return START_NOT_STICKY;
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
