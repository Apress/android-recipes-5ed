package com.androidrecipes.mapper;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerMapActivity extends FragmentActivity implements
        RadioGroup.OnCheckedChangeListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter, OnMapReadyCallback {
    private static final String TAG = "AndroidRecipes";
    private static final int REQUEST_CODE_PERMISSIONS = 10;

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

        // Wire up the map type selector UI
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
        mMap = null;
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
     * OnMarkerClickListener Methods
     */

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Return true to disable auto-center and info pop-up
        return false;
    }

    /**
     * OnMarkerDragListener Methods
     */

    @Override
    public void onMarkerDrag(Marker marker) {
        // Do something while the marker is moving
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.i("MarkerTest", "Drag " + marker.getTitle()
                + " to " + marker.getPosition());
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d("MarkerTest", "Drag " + marker.getTitle()
                + " from " + marker.getPosition());
    }

    /**
     * OnInfoWindowClickListener Methods
     */

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Act upon the selection event, here we just close the window
        marker.hideInfoWindow();
    }

    /**
     * InfoWindowAdapter Methods
     */

    /*
     * Return a content view to be placed inside a standard info window. Only
     * called if getInfoWindow() returns null.
     */
    @Override
    public View getInfoContents(Marker marker) {
        //Try returning createInfoView() here instead
        return null;
    }

    /*
     * Return the entire info window to be displayed.
     */
    @Override
    public View getInfoWindow(Marker marker) {
        View content = createInfoView(marker);
        content.setBackgroundResource(R.drawable.background);
        return content;
    }

    /*
     * Private helper method to construct the content view
     */
    private View createInfoView(Marker marker) {
        // We have no parent for layout, so pass null
        View content = getLayoutInflater().inflate(
                R.layout.info_window, null);
        ImageView image = (ImageView) content
                .findViewById(R.id.image);
        TextView text = (TextView) content
                .findViewById(R.id.text);

        image.setImageResource(R.drawable.ic_launcher);
        text.setText(marker.getTitle());

        return content;
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

        // Monitor interaction with marker elements
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        // Set our application to serve views for the info windows
        mMap.setInfoWindowAdapter(this);
        // Monitor click events on info windows
        mMap.setOnInfoWindowClickListener(this);

        // Google HQ 37.427,-122.099
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.4218, -122.0840))
                .title("Google HQ")
                // Show an image resource from our app as the marker
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.logo))
                //Reduce the opacity
                .alpha(0.6f));
        //Make this marker draggable on the map
        marker.setDraggable(true);

        // Subtract 0.01 degrees
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.4118, -122.0740))
                .title("Neighbor #1")
                .snippet("Best Restaurant in Town")
                // Show a default marker, in the default color
                .icon(BitmapDescriptorFactory.defaultMarker()));

        // Add 0.01 degrees
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.4318, -122.0940))
                .title("Neighbor #2")
                .snippet("Worst Restaurant in Town")
                // Show a default marker, with a blue tint
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Center and zoom the map simultaneously
        LatLng mapCenter = new LatLng(37.4218, -122.0840);
        CameraUpdate newCamera = CameraUpdateFactory
                .newLatLngZoom(mapCenter, 13);
        mMap.moveCamera(newCamera);

        RadioGroup typeSelect = (RadioGroup) findViewById(R.id.group_maptype);
        typeSelect.setEnabled(true);
    }
}
