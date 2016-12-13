package com.androidrecipes.regionmonitor;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Locale;

public class RegionMonitorActivity extends Activity implements
        OnSeekBarChangeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private static final String TAG = "RegionMonitorActivity";
    //Unique identifier for our single geofence
    private static final String FENCE_ID = "com.androidrecipes.FENCE";
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    private SeekBar mRadiusSlider;
    private TextView mStatusText, mRadiusText;

    private Geofence mCurrentFence;
    private PendingIntent mCallbackIntent;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Wire up the UI connections
        mStatusText = (TextView) findViewById(R.id.status);
        mRadiusText = (TextView) findViewById(R.id.radius_text);
        mRadiusSlider = (SeekBar) findViewById(R.id.radius);
        mRadiusSlider.setOnSeekBarChangeListener(this);
        mRadiusText.setText(mRadiusSlider.getProgress() + " meters");

        //Check if Google Play Services is up to date.
        int errorCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);
        switch (errorCode) {
            case ConnectionResult.SUCCESS:
                //Do nothing, move on
                break;
            default:
                DialogInterface.OnCancelListener onCancelListener
                        = new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                };
                GoogleApiAvailability.getInstance()
                        .showErrorDialogFragment(this, errorCode, 10,
                                onCancelListener);
                return;
        }

        //Create an Intent to trigger our service
        Intent serviceIntent = new Intent(this, RegionMonitorService.class);
        //Create a PendingIntent for Google Services to use with callbacks
        mCallbackIntent = PendingIntent.getService(this, 0, serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Create a client for Google Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);
            return;
        }

        //Connect to all services
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Disconnect when not in the foreground
        mGoogleApiClient.disconnect();
    }

    @SuppressWarnings("MissingPermission")
    public void onSetGeofenceClick(View v) {
        //Obtain the last location from services and radius
        // from the UI
        Location current = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        int radius = mRadiusSlider.getProgress();

        //Create a new Geofence using the Builder
        Geofence.Builder builder = new Geofence.Builder();
        mCurrentFence = builder
                //Unique to this geofence
                .setRequestId(FENCE_ID)
                //Size and location
                .setCircularRegion(
                        current.getLatitude(),
                        current.getLongitude(),
                        radius)
                //Events both in and out of the fence
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT
                        | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(30)
                //Keep alive
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        String text = String.format(Locale.getDefault(),
                "Geofence set at %.3f, %.3f",
                current.getLatitude(), current.getLongitude());
        mStatusText.setText(text);
    }

    @SuppressWarnings("MissingPermission")
    public void onStartMonitorClick(View v) {
        if (mCurrentFence == null) {
            Toast.makeText(this, "Geofence Not Yet Set",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Add the fence to start tracking, the PendingIntent will
        // be triggered with new updates
        ArrayList<Geofence> geofences = new ArrayList<>();
        geofences.add(mCurrentFence);
        GeofencingRequest geofencingRequest
                = new GeofencingRequest.Builder()
                .addGeofences(geofences)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .build();
        LocationServices.GeofencingApi
                .addGeofences(mGoogleApiClient, geofencingRequest,
                        mCallbackIntent)
        .setResultCallback(this);
    }

    public void onStopMonitorClick(View v) {
        //Remove to stop tracking
        LocationServices.GeofencingApi
                .removeGeofences(mGoogleApiClient, mCallbackIntent);
    }

    /**
     * SeekBar Callbacks
     */

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        mRadiusText.setText(seekBar.getProgress() + " meters");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "Google Services Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w(TAG, "Google Services Connection Failure");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission " + permission
                            + " is required for this application to work.");
                    finish();
                    return;
                }
            }
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "Geofence operation successful");
        } else {
            Log.d(TAG, "Geofence operation failed: " + status.getStatusMessage());
        }
    }
}
