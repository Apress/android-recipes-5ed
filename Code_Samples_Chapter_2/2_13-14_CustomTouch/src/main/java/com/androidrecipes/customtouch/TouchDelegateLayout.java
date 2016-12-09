package com.androidrecipes.customtouch;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TouchDelegate;
import android.widget.CheckBox;
import android.widget.FrameLayout;

public class TouchDelegateLayout extends FrameLayout {

    public TouchDelegateLayout(Context context) {
        super(context);
        init(context);
    }

    public TouchDelegateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TouchDelegateLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private CheckBox mButton;

    private void init(Context context) {
        // Create a small child view we want to forward touches to.
        mButton = new CheckBox(context);
        mButton.setText("Tap Anywhere");

        LayoutParams lp = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        addView(mButton, lp);
    }

    /*
     * TouchDelegate is applied to this view (parent) to delegate all touches
     * within the specified rectangle to the CheckBox (child). Here, the
     * rectangle is the entire size of this parent view.
     * 
     * This must be done after the view has a size so we know how big to make
     * the Rect, thus we've chosen to add the delegate in onSizeChanged()
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            // Apply the whole area of this view as the delegate area
            Rect bounds = new Rect(0, 0, w, h);
            TouchDelegate delegate = new TouchDelegate(bounds, mButton);
            setTouchDelegate(delegate);
        }
    }
}
