package com.examples.statictransforms;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class PerspectiveScrollContentView extends LinearLayout {

    /* Adjustable scale factor for child views */
    private static final float SCALE_FACTOR = 0.7f;
    /* Anchor point for transformation.  (0,0) is top left,
     * (1,1) is bottom right.  This is currently set for
     * the bottom middle (0.5, 1)
     */
    private static final float ANCHOR_X = 0.5f;
    private static final float ANCHOR_Y = 1.0f;

    public PerspectiveScrollContentView(Context context) {
        super(context);
        init();
    }

    public PerspectiveScrollContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PerspectiveScrollContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Enable static transformations so getChildStaticTransformation()
        // will be called for each child.
        setStaticTransformationsEnabled(true);
    }

    /*
     * Utility method to calculate the current position of any
     * View in the screen's coordinates
     */
    private int getViewCenter(View view) {
        int[] childCoords = new int[2];
        view.getLocationOnScreen(childCoords);
        int childCenter = childCoords[0] + (view.getWidth() / 2);

        return childCenter;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        HorizontalScrollView scrollView = null;
        if (getParent() instanceof HorizontalScrollView) {
            scrollView = (HorizontalScrollView) getParent();
        }
        if (scrollView == null) {
            return false;
        }

        int childCenter = getViewCenter(child);
        int viewCenter = getViewCenter(scrollView);
        // Calculate the difference between this child and our parent's center
        // That will determine the scale factor applied.
        float delta = Math.min(1.0f, Math.abs(childCenter - viewCenter)
                / (float) viewCenter);
        float scale = Math.max(0.4f, 1.0f - (SCALE_FACTOR * delta));
        float xTrans = child.getWidth() * ANCHOR_X;
        float yTrans = child.getHeight() * ANCHOR_Y;

        //Clear any existing transformation
        t.clear();
        //Set the transformation for the child view
        t.getMatrix().setScale(scale, scale, xTrans, yTrans);

        return true;
    }
}
