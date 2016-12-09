package com.androidrecipes.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ServiceActivity extends Activity implements View.OnClickListener {

    Button enableButton, disableButton;
    TextView statusView;

    TrackerService trackerService;
    Intent serviceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        enableButton = (Button) findViewById(R.id.enable);
        enableButton.setOnClickListener(this);
        disableButton = (Button) findViewById(R.id.disable);
        disableButton.setOnClickListener(this);
        statusView = (TextView) findViewById(R.id.status);

        serviceIntent = new Intent(this, TrackerService.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Starting the service makes it stick, regardless of bindings
        startService(serviceIntent);
        //Bind to the service
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!trackerService.isTracking()) {
            //Stopping the service let's it die once unbound
            stopService(serviceIntent);
        }
        //Unbind from the service
        unbindService(serviceConnection);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enable:
                trackerService.startTracking();
                break;
            case R.id.disable:
                trackerService.stopTracking();
                break;
            default:
                break;
        }
        updateStatus();
    }

    private void updateStatus() {
        if (trackerService.isTracking()) {
            statusView.setText(String.format("Tracking enabled.  %d locations logged.", trackerService.getLocationsCount()));
        } else {
            statusView.setText("Tracking not currently enabled.");
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            trackerService = ((TrackerService.TrackerBinder) service).getService();
            updateStatus();
        }

        public void onServiceDisconnected(ComponentName className) {
            trackerService = null;
        }
    };
}