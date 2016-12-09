package com.androidrecipes.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class TrackerService extends Service implements LocationListener {

    private static final String LOGTAG = "TrackerService";

    private LocationManager manager;
    private ArrayList<Location> storedLocations;

    private boolean isTracking = false;

    /* Service Setup Methods */
    @Override
    public void onCreate() {
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        storedLocations = new ArrayList<Location>();
        Log.i(LOGTAG, "Tracking Service Running...");
    }

    public void startTracking() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }
        Toast.makeText(this, "Starting Tracker", Toast.LENGTH_SHORT).show();
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, this);

        isTracking = true;
    }

    public void stopTracking() {
        Toast.makeText(this, "Stopping Tracker", Toast.LENGTH_SHORT).show();
        manager.removeUpdates(this);
        isTracking = false;
    }

    public boolean isTracking() {
        return isTracking;
    }

    @Override
    public void onDestroy() {
        manager.removeUpdates(this);
        Log.i(LOGTAG, "Tracking Service Stopped...");
    }

    /* Service Access Methods */
    public class TrackerBinder extends Binder {
        TrackerService getService() {
            return TrackerService.this;
        }
    }

    private final IBinder binder = new TrackerBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int getLocationsCount() {
        return storedLocations.size();
    }

    public ArrayList<Location> getLocations() {
        return storedLocations;
    }

    /* LocationListener Methods */
    @Override
    public void onLocationChanged(Location location) {
        Log.i("TrackerService", "Adding new location");
        storedLocations.add(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
