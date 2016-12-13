package com.androidrecipes.mapper;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidrecipes.mapper.ShapeAdapter.Region;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class ShapeMapActivity extends FragmentActivity implements
        RadioGroup.OnCheckedChangeListener,
        ShapeAdapter.OnRegionSelectedListener, OnMapReadyCallback {
    private static final String TAG = "AndroidRecipes";

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //Wire up the map type selector UI
        RadioGroup typeSelect = (RadioGroup) findViewById(R.id.group_maptype);
        typeSelect.setEnabled(false);
        typeSelect.setOnCheckedChangeListener(this);
        typeSelect.check(R.id.type_normal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * OnCheckedChangeListener Methods
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.type_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.type_normal:
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    /**
     * OnRegionSelectedListener Methods
     */
    @Override
    public void onRegionSelected(Region selectedRegion) {
        Toast.makeText(this, selectedRegion.getName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoRegionSelected() {
        Toast.makeText(this, "No Region", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ShapeAdapter adapter = new ShapeAdapter(mMap);
        adapter.setOnRegionSelectedListener(this);

        adapter.addRectangularRegion("Google HQ",
                new LatLng(37.4168, -122.0890),
                new LatLng(37.4268, -122.0790));
        adapter.addCircularRegion("Neighbor #1",
                new LatLng(37.4118, -122.0740), 400);
        adapter.addCircularRegion("Neighbor #2",
                new LatLng(37.4318, -122.0940), 400);

        //Center and zoom map simultaneously
        LatLng mapCenter = new LatLng(37.4218, -122.0840);
        CameraUpdate newCamera = CameraUpdateFactory.newLatLngZoom(mapCenter, 13);
        mMap.moveCamera(newCamera);

        RadioGroup typeSelect = (RadioGroup) findViewById(R.id.group_maptype);
        typeSelect.setEnabled(true);
    }
}
