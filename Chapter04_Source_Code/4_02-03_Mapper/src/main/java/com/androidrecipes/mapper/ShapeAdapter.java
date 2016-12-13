package com.androidrecipes.mapper;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class ShapeAdapter implements OnMapClickListener {

    private static final float STROKE_SELECTED = 6.0f;
    private static final float STROKE_NORMAL = 2.0f;
    /* Colors for the drawn regions */
    private static final int COLOR_STROKE = Color.RED;
    private static final int COLOR_FILL = Color.argb(127, 0, 0, 255);

    /*
     * External interface to notify listeners of a change in
     * the selected region based on user taps
     */
    public interface OnRegionSelectedListener {
        public void onRegionSelected(Region selectedRegion);

        public void onNoRegionSelected();
    }

    /*
     * Base definition of an interactive region on the map.
     * Defines methods to change display and check user taps
     */
    public static abstract class Region {
        private String mRegionName;

        public Region(String regionName) {
            mRegionName = regionName;
        }

        public String getName() {
            return mRegionName;
        }

        //Check if a location is inside this region
        public abstract boolean hitTest(LatLng point);

        //Change the display of the region base on selection
        public abstract void setSelected(boolean isSelected);
    }

    /*
     * Implementation of a region drawn as a circle
     */
    private static class CircleRegion extends Region {
        private Circle mCircle;

        public CircleRegion(String name, Circle circle) {
            super(name);
            mCircle = circle;
        }

        @Override
        public boolean hitTest(LatLng point) {
            final LatLng center = mCircle.getCenter();
            float[] result = new float[1];
            Location.distanceBetween(center.latitude, center.longitude,
                    point.latitude, point.longitude,
                    result);

            return (result[0] < mCircle.getRadius());
        }

        @Override
        public void setSelected(boolean isSelected) {
            mCircle.setStrokeWidth(isSelected ? STROKE_SELECTED : STROKE_NORMAL);
        }

    }

    /*
     * Implementation of a region drawn as a rectangle
     */
    private static class RectRegion extends Region {
        private Polygon mRect;
        private LatLngBounds mRectBounds;

        public RectRegion(String name, Polygon rect, LatLng southwest, LatLng northeast) {
            super(name);
            mRect = rect;
            mRectBounds = new LatLngBounds(southwest, northeast);
        }

        @Override
        public boolean hitTest(LatLng point) {
            return mRectBounds.contains(point);
        }

        @Override
        public void setSelected(boolean isSelected) {
            mRect.setStrokeWidth(isSelected ? STROKE_SELECTED : STROKE_NORMAL);
        }
    }

    private GoogleMap mMap;

    private OnRegionSelectedListener mRegionSelectedListener;
    private ArrayList<Region> mRegions;
    private Region mCurrentRegion;

    public ShapeAdapter(GoogleMap map) {
        mRegions = new ArrayList<Region>();

        mMap = map;
        mMap.setOnMapClickListener(this);
    }

    public void setOnRegionSelectedListener(OnRegionSelectedListener listener) {
        mRegionSelectedListener = listener;
    }

    /*
     * Construct and add a new circular region around the given point.
     */
    public void addCircularRegion(String name, LatLng center, double radius) {
        CircleOptions options = new CircleOptions()
                .center(center)
                .radius(radius);
        options.strokeWidth(STROKE_NORMAL).strokeColor(COLOR_STROKE).fillColor(COLOR_FILL);

        Circle c = mMap.addCircle(options);
        mRegions.add(new CircleRegion(name, c));
    }

    /*
     * Construct and add a new rectangular region around with the given boundaries
     */
    public void addRectangularRegion(String name, LatLng southwest, LatLng northeast) {
        PolygonOptions options = new PolygonOptions().add(
                new LatLng(southwest.latitude, southwest.longitude),
                new LatLng(southwest.latitude, northeast.longitude),
                new LatLng(northeast.latitude, northeast.longitude),
                new LatLng(northeast.latitude, southwest.longitude));
        options.strokeWidth(STROKE_NORMAL).strokeColor(COLOR_STROKE).fillColor(COLOR_FILL);

        Polygon p = mMap.addPolygon(options);
        mRegions.add(new RectRegion(name, p, southwest, northeast));
    }

    /*
     * Handle incoming tap events from the map object.
     * Determine which region element may have been selected. If
     * regions overlap at this point, the first added will be selected.
     */
    @Override
    public void onMapClick(LatLng point) {
        Region newSelection = null;
        //Find and select the tapped region
        for (Region region : mRegions) {
            if (region.hitTest(point) && newSelection == null) {
                region.setSelected(true);
                newSelection = region;
            } else {
                region.setSelected(false);
            }
        }

        if (mCurrentRegion != newSelection) {
            //Notify and update the change
            if (newSelection != null && mRegionSelectedListener != null) {
                mRegionSelectedListener.onRegionSelected(newSelection);
            } else if (mRegionSelectedListener != null) {
                mRegionSelectedListener.onNoRegionSelected();
            }

            mCurrentRegion = newSelection;
        }
    }

}
