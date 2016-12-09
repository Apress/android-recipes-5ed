package com.androidrecipes.sticky;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class OperationsManager extends IntentService {

    public static final String ACTION_EVENT = "ACTION_EVENT";
    public static final String ACTION_WARNING = "ACTION_WARNING";
    public static final String ACTION_ERROR = "ACTION_ERROR";
    public static final String EXTRA_NAME = "eventName";

    private static final String LOGTAG = "EventLogger";

    private IntentFilter matcher;

    public OperationsManager() {
        super("OperationsManager");
        //Create the filter for matching incoming requests
        matcher = new IntentFilter();
        matcher.addAction(ACTION_EVENT);
        matcher.addAction(ACTION_WARNING);
        matcher.addAction(ACTION_ERROR);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Check for a valid request
        if (!matcher.matchAction(intent.getAction())) {
            Toast.makeText(this, "OperationsManager: Invalid Request", Toast.LENGTH_SHORT).show();
            return;
        }

        //Handle each request directly in this method.  Don't create more threads.
        if (TextUtils.equals(intent.getAction(), ACTION_EVENT)) {
            logEvent(intent.getStringExtra(EXTRA_NAME));
        }
        if (TextUtils.equals(intent.getAction(), ACTION_WARNING)) {
            logWarning(intent.getStringExtra(EXTRA_NAME));
        }
        if (TextUtils.equals(intent.getAction(), ACTION_ERROR)) {
            logError(intent.getStringExtra(EXTRA_NAME));
        }
    }

    private void logEvent(String name) {
        try {
            //Simulate a long network operation by sleeping
            Thread.sleep(5000);
            Log.i(LOGTAG, name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void logWarning(String name) {
        try {
            //Simulate a long network operation by sleeping
            Thread.sleep(5000);
            Log.w(LOGTAG, name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void logError(String name) {
        try {
            //Simulate a long network operation by sleeping
            Thread.sleep(5000);
            Log.e(LOGTAG, name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
