package com.androidrecipes.usermotionactivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class UserMotionService extends IntentService {
    private static final String TAG = "UserMotionService";

    /*
     * Callback interface for detected activity type changes
     */
    public interface OnActivityChangedListener {
        public void onUserActivityChanged(int bestChoice, int bestConfidence,
                                          ActivityRecognitionResult newActivity);
    }

    /* Last detected activity type */
    private DetectedActivity mLastKnownActivity;
    /*
     * Marshals requests from the background thread so the callbacks
     * can be made on the main (UI) thread.
     */
    private CallbackHandler mHandler;

    private static class CallbackHandler extends Handler {
        /* Callback for activity changes */
        private OnActivityChangedListener mCallback;

        public void setCallback(OnActivityChangedListener callback) {
            mCallback = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mCallback != null) {
                //Read payload data out of the message and fire callback
                ActivityRecognitionResult newActivity = (ActivityRecognitionResult) msg.obj;
                mCallback.onUserActivityChanged(msg.arg1, msg.arg2, newActivity);
            }
        }
    }

    public UserMotionService() {
        //String is used to name the background thread created
        super("UserMotionService");
        mHandler = new CallbackHandler();
    }

    public void setOnActivityChangedListener(OnActivityChangedListener listener) {
        mHandler.setCallback(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "Service is stopping...");
    }

    /*
     * Incoming action events from the framework will come
     * in here.  This is called on a background thread, so
     * we can do long processing here if we wish.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            //Extract the result from the Intent
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);
            DetectedActivity activity = result.getMostProbableActivity();
            Log.v(TAG, "New User Activity Event");

            //If the highest probability is UNKNOWN, but the confidence is low,
            // check if another exists and select it instead
            if (activity.getType() == DetectedActivity.UNKNOWN
                    && activity.getConfidence() < 60
                    && result.getProbableActivities().size() > 1) {
                //Select the next probable element
                activity = result.getProbableActivities().get(1);
            }

            //On a change in activity, alert the callback
            if (mLastKnownActivity == null
                    || mLastKnownActivity.getType() != activity.getType()
                    || mLastKnownActivity.getConfidence() != activity.getConfidence()) {
                //Pass the results to the main thread inside a Message
                Message msg = Message.obtain(null,
                        0,                         //what
                        activity.getType(),        //arg1
                        activity.getConfidence(),  //arg2
                        result);
                mHandler.sendMessage(msg);
            }
            mLastKnownActivity = activity;
        }
    }

    /*
     * This is called when the Activity wants to bind to the
     * service.  We have to provide a wrapper around this instance
     * to pass it back.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*
     * This is a simple wrapper that we can pass to the Activity
     * to allow it direct access to this service.
     */
    private LocalBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public UserMotionService getService() {
            return UserMotionService.this;
        }
    }

    /*
     * Utility to get a good display name for each state
     */
    public static String getActivityName(DetectedActivity activity) {
        switch (activity.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return "Driving";
            case DetectedActivity.ON_BICYCLE:
                return "Biking";
            case DetectedActivity.ON_FOOT:
                return "Walking";
            case DetectedActivity.STILL:
                return "Not Moving";
            case DetectedActivity.TILTING:
                return "Tilting";
            case DetectedActivity.UNKNOWN:
            default:
                return "No Clue";
        }
    }
}
