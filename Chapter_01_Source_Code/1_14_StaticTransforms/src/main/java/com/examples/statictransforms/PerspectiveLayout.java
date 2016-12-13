package com.examples.statictransforms;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class PerspectiveLayout extends LinearLayout {

    public PerspectiveLayout(Context context) {
        super(context);
        init();
    }

    public PerspectiveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PerspectiveLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Enable static transformations so getChildStaticTransformation()
        // will be called for each child.
        setStaticTransformationsEnabled(true);
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        // Clear any existing transformation
        t.clear();

        if (getOrientation() == HORIZONTAL) {
            // Scale children based on distance from left edge
            float delta = 1.0f - ((float) child.getLeft() / getWidth());

            t.getMatrix().setScale(delta, delta, child.getWidth() / 2,
                    child.getHeight() / 2);
        } else {
            // Scale children based on distance from top edge
            float delta = 1.0f - ((float) child.getTop() / getHeight());

            t.getMatrix().setScale(delta, delta, child.getWidth() / 2,
                    child.getHeight() / 2);
            //Also apply a fade effect based on its location
            t.setAlpha(delta);
        }
        return true;
    }
}
