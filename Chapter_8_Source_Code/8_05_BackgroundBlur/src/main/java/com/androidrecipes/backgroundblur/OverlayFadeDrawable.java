package com.androidrecipes.backgroundblur;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

public class OverlayFadeDrawable extends LayerDrawable {
    /*
     * Implementation of a Drawable container to hold our normal
     * and blurred images as layers
     */
    public OverlayFadeDrawable(Drawable base, Drawable overlay) {
        super(new Drawable[]{base, overlay});
    }

    /*
     * Force a redraw when the level value is externally changed
     */
    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        final Drawable base = getDrawable(0);
        final Drawable overlay = getDrawable(1);
        //Get the level as a percentage of the maximum value
        final float percent = getLevel() / 10000f;
        int setAlpha = Math.round(percent * 0xFF);

        //Optimize for end-cases to avoid overdraw
        if (setAlpha == 255) {
            overlay.draw(canvas);
            return;
        }
        if (setAlpha == 0) {
            base.draw(canvas);
            return;
        }

        //Draw composite if in-between
        base.draw(canvas);

        overlay.setAlpha(setAlpha);
        overlay.draw(canvas);
        overlay.setAlpha(0xFF);
    }
}
