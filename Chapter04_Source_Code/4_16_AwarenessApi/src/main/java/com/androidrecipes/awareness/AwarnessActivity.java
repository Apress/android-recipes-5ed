package com.androidrecipes.awareness;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.BeaconFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.TimeZone;

public class AwarnessActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "AwarenessActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    static final String BEACON_NAMESPACE = "";
    static final String BEACON_TYPE = "";
    // 12 am
    private static final long START_TIME = 12L * 60L * 60L * 1000L;
    // 1 pm
    private static final long END_TIME = 13L * 60L * 60L * 1000L;
    static final String KEY_BEACON = "beacon";
    static final String KEY_WALKING = "walking";
    static final String KEY_HEADPHONE = "headphone";
    static final String KEY_LOCATION = "location";
    static final String KEY_LUNCHTIME = "lunchtime";
    private static final String ACTIVITY_RECOGNITION_PERMISSION = "com.google.android.gms.permission.ACTIVITY_RECOGNITION";
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Verify play services is active and up to date
        int resultCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);
        switch (resultCode) {
            case ConnectionResult.SUCCESS:
                Log.d(TAG, "Google Play Services is ready to go!");
                break;
            default:
                showPlayServicesError(resultCode);
                return;
        }

mApiClient = new GoogleApiClient.Builder(this)
        .addApi(Awareness.API)
        .addConnectionCallbacks(this)
        .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.fenceActivateBtn).setEnabled(false);
        mApiClient.connect();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mApiClient.disconnect();
    }

    /**
     * When Play Services is missing or at the wrong version, the client
     * library will assist with a dialog to help the user update.
     **/
    private void showPlayServicesError(int errorCode) {
        GoogleApiAvailability.getInstance()
                .showErrorDialogFragment(this, errorCode, 10,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                ACTIVITY_RECOGNITION_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            ACTIVITY_RECOGNITION_PERMISSION},
                    REQUEST_CODE_PERMISSIONS);
            return;
        }

        // Permission check passed - enable button
        findViewById(R.id.fenceActivateBtn).setEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if(grantResult != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission " + permission
                            + " is required for this application to work.");
                    finish();
                    return;
                }
            }
        }
    }

@SuppressWarnings("MissingPermission")
public void doActivateFence(View view) {
    Intent intent = new Intent(getApplicationContext(), AwarenessTriggeredService.class);
    intent.setAction(AwarenessTriggeredService.ACTION_AWARENESS_TRIGGERED);
    AwarenessFence awarenessFence;
    String fenceKey;
    RadioGroup fenceSelection = (RadioGroup) findViewById(R.id.fenceSelection);
    int checkedRadioButtonId = fenceSelection.getCheckedRadioButtonId();
    switch (checkedRadioButtonId) {
        case R.id.beaconFence:
            awarenessFence = BeaconFence.found(BeaconState.TypeFilter.with(BEACON_NAMESPACE, BEACON_TYPE));
            fenceKey = KEY_BEACON;
            break;
        case R.id.walkingFence:
            awarenessFence = DetectedActivityFence.starting(DetectedActivityFence.WALKING);
            fenceKey = KEY_WALKING;
            break;
        case R.id.headphoneFence:
            awarenessFence = HeadphoneFence.pluggingIn();
            fenceKey = KEY_HEADPHONE;
            break;
        case R.id.locationFence:
            awarenessFence = LocationFence.entering(37.4218, -122.0840, 500);
            fenceKey = KEY_LOCATION;
            break;
        case R.id.lunchtimeFence:
            awarenessFence = TimeFence.inDailyInterval(TimeZone.getDefault(), START_TIME, END_TIME);
            fenceKey = KEY_LUNCHTIME;
            break;
        default:
            return;
    }

    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0,
            intent, PendingIntent.FLAG_CANCEL_CURRENT);
    FenceUpdateRequest fenceUpdateRequest = new FenceUpdateRequest.Builder()
            .addFence(fenceKey, awarenessFence, pendingIntent)
            .build();
    Awareness.FenceApi.updateFences(mApiClient, fenceUpdateRequest);
}
}
