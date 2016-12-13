package com.androidrecipes.viewoverlay;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnTouchListener {

    private RadioGroup mOptions;

    private ArrayList<Drawable> mMarkers;
    private Drawable mTrackingMarker;
    private Point mTrackingPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Receive touch events for the view we want to draw on
        findViewById(R.id.textview).setOnTouchListener(this);

        mOptions = (RadioGroup) findViewById(R.id.container_options);

        mMarkers = new ArrayList<Drawable>();
    }

    /*
     * Touch events from the view we are monitoring
     * will be delivered here.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (mOptions.getCheckedRadioButtonId()) {
            case R.id.option_box:
                handleEvent(R.id.option_box, v, event);
                break;
            case R.id.option_arrow:
                handleEvent(R.id.option_arrow, v, event);
                break;
            default:
                return false;
        }
        return true;
    }

    /*
     * Process touch events when user has selected to draw a box
     */
    private void handleEvent(int optionId, View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Drawable current = markerAt(x, y);
                if (current == null) {
                    //Add a new marker on a new touch
                    switch (optionId) {
                        case R.id.option_box:
                            mTrackingMarker = addBox(v, x, y);
                            mTrackingPoint = new Point(x, y);
                            break;
                        case R.id.option_arrow:
                            mTrackingMarker = addFlag(v, x, y);
                            break;
                    }
                } else {
                    //Remove the existing marker
                    removeMarker(v, current);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //Update the current marker as we move
                if (mTrackingMarker != null) {
                    switch (optionId) {
                        case R.id.option_box:
                            resizeBox(v, mTrackingMarker, mTrackingPoint, x, y);
                            break;
                        case R.id.option_arrow:
                            offsetFlag(v, mTrackingMarker, x, y);
                            break;
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //Clear state when gesture is over
                mTrackingMarker = null;
                mTrackingPoint = null;
                break;
        }
    }

    /*
     * Add a new resizable box at the given coordinate
     */
    private Drawable addBox(View v, int x, int y) {
        Drawable box = getResources().getDrawable(R.drawable.box);

        //Start with a zero size box at the touch point
        Rect bounds = new Rect(x, y, x, y);
        box.setBounds(bounds);

        //Add to the ViewOverlay
        mMarkers.add(box);
        v.getOverlay().add(box);

        return box;
    }

    /*
     * Update an existing box to resize based on the given coordinate
     */
    private void resizeBox(View v, Drawable target, Point trackingPoint, int x, int y) {
        Rect bounds = new Rect(target.getBounds());
        //If the new touch point is to the left of the tracking point, grow left
        // Otherwise, grow to the right
        if (x < trackingPoint.x) {
            bounds.left = x;
        } else {
            bounds.right = x;
        }

        //If the new touch point is above the tracking point, grow up
        // Otherwise, grow down
        if (y < trackingPoint.y) {
            bounds.top = y;
        } else {
            bounds.bottom = y;
        }

        //Update drawable bounds and redraw
        target.setBounds(bounds);
        v.invalidate();
    }

    /*
     * Add a new flag marker at the given coordinate
     */
    private Drawable addFlag(View v, int x, int y) {
        //Make a new marker drawable
        Drawable marker = getResources().getDrawable(R.drawable.flag_arrow);

        //Create bounds to match image size
        Rect bounds = new Rect(0, 0,
                marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        //Center marker bottom around coordinate
        bounds.offset(x - (bounds.width() / 2), y - bounds.height());
        marker.setBounds(bounds);
        //Add to the overlay
        mMarkers.add(marker);
        v.getOverlay().add(marker);

        return marker;
    }

    /*
     * Update the position of an existing flag marker
     */
    private void offsetFlag(View v, Drawable marker, int x, int y) {
        Rect bounds = new Rect(marker.getBounds());
        //Move drawable bounds to the next align with the new coordinate
        bounds.offset(x - bounds.left - (bounds.width() / 2),
                y - bounds.top - bounds.height());
        //Update and redraw
        marker.setBounds(bounds);
        v.invalidate();
    }

    /*
     * Remove the requested marker item
     */
    private void removeMarker(View v, Drawable marker) {
        mMarkers.remove(marker);
        v.getOverlay().remove(marker);
    }

    /*
     * Find the first marker that contains the requested
     * coordinate, if one exists.
     */
    private Drawable markerAt(int x, int y) {
        //Return the first marker found containing the given point
        for (Drawable marker : mMarkers) {
            if (marker.getBounds().contains(x, y)) {
                return marker;
            }
        }

        return null;
    }
}
