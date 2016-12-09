package com.androidrecipes.rotation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class LockActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        //Get handle to the button resource
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        //Set the default state before adding the listener
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            toggle.setChecked(true);
        } else {
            toggle.setChecked(false);
        }
        //Attach the listener to the button
        toggle.setOnCheckedChangeListener(new OrientationLockListener());
    }

    private class OrientationLockListener implements CompoundButton.OnCheckedChangeListener {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int current = getResources().getConfiguration().orientation;
            if (isChecked) {
                switch (current) {
                    case Configuration.ORIENTATION_LANDSCAPE:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case Configuration.ORIENTATION_PORTRAIT:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    default:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
    }

    ;

}
