package com.androidrecipes.regionmonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class RegionMonitorService extends Service {
    //Unique action to identify start requests vs. events
    public static final String ACTION_INIT =
            "com.androidrecipes.regionmonitor.ACTION_INIT";
    private static final String TAG = "RegionMonitorService";
    private static final int NOTE_ID = 100;
    private NotificationManager mNoteManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mNoteManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Post a system notification when the service starts
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Geofence Service");
        builder.setContentText("Waiting for transition...");
        builder.setOngoing(true);

        Notification note = builder.build();
        mNoteManager.notify(NOTE_ID, note);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Nothing to do yet, just starting the service
        if (ACTION_INIT.equals(intent.getAction())) {
            //We don't care if this service dies unexpectedly
            return START_NOT_STICKY;
        }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            //Log any errors
            Log.w(TAG, "Error monitoring region: "
                    + geofencingEvent.getErrorCode());
        } else {
            //Update the ongoing notification from the new event
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setDefaults(Notification.DEFAULT_SOUND
                    | Notification.DEFAULT_LIGHTS);
            builder.setAutoCancel(true);

            int transitionType = geofencingEvent.getGeofenceTransition();

            //Check whether we entered or exited the region
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                builder.setContentTitle("Geofence Transition");
                builder.setContentText("Entered your Geofence");
            } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                builder.setContentTitle("Geofence Transition");
                builder.setContentText("Exited your Geofence");
            }

            Notification note = builder.build();
            mNoteManager.notify(NOTE_ID, note);
        }

        //We don't care if this service dies unexpectedly
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //When the service dies, cancel our ongoing notification
        mNoteManager.cancel(NOTE_ID);
    }

    /* We are not binding to this service */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
