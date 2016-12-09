package com.androidrecipes.logger;

import android.app.Activity;
import android.os.Bundle;

public class LoggerActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //This statement only printed in debug
        Logger.d("Activity Created");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //This statement only printed in debug
        Logger.d("Activity Resume at %d", System.currentTimeMillis());
        //This statement always printed
        Logger.i("It is now %d", System.currentTimeMillis());
    }

    @Override
    protected void onPause() {
        super.onPause();
        //This statement only printed in debug
        Logger.d("Activity Pause at %d", System.currentTimeMillis());
        //This always printed
        Logger.w("No, don't leave!");
    }
}
