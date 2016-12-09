package com.androidrecipes.alarms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Perform an interesting operation, we'll just display the current time
        Calendar now = Calendar.getInstance();
        DateFormat formatter = SimpleDateFormat.getTimeInstance();
        Toast.makeText(this, formatter.format(now.getTime()), Toast.LENGTH_SHORT).show();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
