package com.androidrecipes.backgroundblur;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class HeaderView extends View {

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * Measure this view's height to always be 85% of the
     * measured height from the parent view (ListView)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View parent = (View) getParent();
        int parentHeight = parent.getMeasuredHeight();

        int height = Math.round(parentHeight * 0.85f);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, height);
    }
}
