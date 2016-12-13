package com.androidrecipes.mapper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.RadioGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class BasicMapActivity extends FragmentActivity implements
        RadioGroup.OnCheckedChangeListener, OnMapReadyCallback {
    private static final String TAG = "AndroidRecipes";
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

        mMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Wire up the map type selector UI - disable until we get a reference to GoogleMap
        RadioGroup typeSelect = (RadioGroup) findViewById(R.id.group_maptype);
        typeSelect.setEnabled(false);
        typeSelect.setOnCheckedChangeListener(this);
        typeSelect.check(R.id.type_normal);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);
            return;
        }

        RadioGroup typeSelect = (RadioGroup) findViewById(R.id.group_maptype);
        typeSelect.setEnabled(true);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onResume();
        if (mMap != null) {
            //Disable user location when not visible
            //noinspection MissingPermission
            mMap.setMyLocationEnabled(false);
        }
    }

    /*
     * When Play Services is missing or at the wrong version, the client
     * library will assist with a dialog to help the user update.
     */
    private void showPlayServicesError(int errorCode) {
        // Get the error dialog from Google Play services
        GoogleApiAvailability.getInstance()
                .showErrorDialogFragment(this, errorCode, 1,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
    }

    /**
     * OnCheckedChangeListener Methods
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.type_satellite:
                //Show the satellite map view
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.type_normal:
            default:
                //Show the normal map view
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        //Quickly see if our last known user location is valid, and center
        // the map around that point.  If not, use a default location.
        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location =
                manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        LatLng mapCenter;
        if (location != null) {
            mapCenter = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            //Use a default location
            mapCenter = new LatLng(37.4218, -122.0840);
        }

        //Center and zoom the map simultaneously
        CameraUpdate newCamera = CameraUpdateFactory.newLatLngZoom(mapCenter, 13);
        mMap.moveCamera(newCamera);
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

}