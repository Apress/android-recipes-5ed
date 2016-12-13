package com.androidrecipes.awareness;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.snapshot.BeaconStateResult;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.concurrent.TimeUnit;


@SuppressWarnings("MissingPermission")
public class AwarenessTriggeredService extends IntentService {
    static final String ACTION_AWARENESS_TRIGGERED = "com.androidrecipes.awareness.AWARENESS_TRIGGERED";

    public AwarenessTriggeredService() {
        super("AwarenessTriggeredService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && ACTION_AWARENESS_TRIGGERED.equals(intent.getAction())) {
            FenceState fenceState = FenceState.extract(intent);
            if (fenceState.getCurrentState() != FenceState.TRUE) {
                return;
            }

            String fenceKey = fenceState.getFenceKey();
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Awareness.API)
                    .build();
            ConnectionResult connectionResult = googleApiClient.blockingConnect();
            if (connectionResult.isSuccess()) {
                switch (fenceKey) {
                    case AwarnessActivity.KEY_BEACON:
                        showBeaconState(googleApiClient);
                        break;
                    case AwarnessActivity.KEY_WALKING:
                        showIsWalkingInfo(googleApiClient);
                        break;
                    case AwarnessActivity.KEY_LOCATION:
                        showLocationInfo(googleApiClient);
                        break;
                    case AwarnessActivity.KEY_HEADPHONE:
                        showHeadphoneInfo(googleApiClient);
                        break;
                    case AwarnessActivity.KEY_LUNCHTIME:
                        showLunchtimeInfo();
                        break;
                }
            }
        }
    }

    private void showLunchtimeInfo() {
        showNotification("Lunchtime!", "It is now.");
    }

    private void showHeadphoneInfo(GoogleApiClient googleApiClient) {
        HeadphoneState headphoneState = Awareness.SnapshotApi.getHeadphoneState(googleApiClient)
                .await(5, TimeUnit.SECONDS).getHeadphoneState();

        String stateDescription;
        if (headphoneState.getState() == HeadphoneState.PLUGGED_IN) {
            stateDescription = "Headphones are plugged in.";
        } else {
            stateDescription = "Headphones are NOT plugged in.";
        }
        showNotification("Headphone state changed", stateDescription);
    }

    private void showLocationInfo(GoogleApiClient googleApiClient) {
        Location location = Awareness.SnapshotApi
                .getLocation(googleApiClient)
                .await(5, TimeUnit.SECONDS).getLocation();
        showNotification("Location changed!", "New location: " + location.toString());
    }

    private void showIsWalkingInfo(GoogleApiClient googleApiClient) {
        ActivityRecognitionResult activityRecognitionResult = Awareness
                .SnapshotApi.getDetectedActivity(googleApiClient)
                .await(5, TimeUnit.SECONDS).getActivityRecognitionResult();
        if (activityRecognitionResult.getMostProbableActivity().getType()
                == DetectedActivity.WALKING) {
            showNotification("Activity detected!", "You are currently walking!");
        }
    }

    private void showBeaconState(GoogleApiClient googleApiClient) {
        BeaconState.TypeFilter typeFilter = BeaconState.TypeFilter
                .with(AwarnessActivity.BEACON_NAMESPACE, AwarnessActivity.BEACON_TYPE);
        PendingResult<BeaconStateResult> pendingResult = Awareness.SnapshotApi
                .getBeaconState(googleApiClient, typeFilter);
        BeaconState beaconState = pendingResult.await(5, TimeUnit.SECONDS).getBeaconState();
        BeaconState.BeaconInfo beaconInfo = beaconState.getBeaconInfo().get(0);
        showNotification("Beacon found!", "Content: " + Base64
                .encodeToString(beaconInfo.getContent(), Base64.DEFAULT));
    }

    private void showNotification(String title, String content) {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_mood_black_24dp)
                .build();
        NotificationManagerCompat.from(this).notify(101, notification);
    }
}
