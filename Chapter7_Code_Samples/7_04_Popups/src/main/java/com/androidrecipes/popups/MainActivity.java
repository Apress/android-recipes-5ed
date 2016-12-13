package com.androidrecipes.popups;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class MainActivity extends Activity implements View.OnTouchListener {

    PopupWindow mOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View popupContent = getLayoutInflater().inflate(R.layout.popup, null);

        mOverlay = new PopupWindow();
        //Wrap content view
        mOverlay.setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        //Fixed window size
        mOverlay.setWidth(getResources().getDimensionPixelSize(R.dimen.popupWidth));
        mOverlay.setHeight(getResources().getDimensionPixelSize(R.dimen.popupHeight));
        mOverlay.setContentView(popupContent);
        mOverlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));

        //Set a custom animation enter/exit pair, or 0 to disable animations
        // You can also use animation styles defined in the platform, such as 
        // android.R.style.Animation_Toast to use the same fade animation as a
        // standard Toast window
        mOverlay.setAnimationStyle(R.style.PopupAnimation);

        //Default behavior is not to allow any elements in the PopupWindow to be interactive,
        // but to enable touch events to be delivered directly to the PopupWindow. All
        // outside touches will be delivered to the main (Activity) window.
        mOverlay.setTouchInterceptor(this);

        //Call setFocusable() to enable elements in the PopupWindow to take focus, which
        // will also enable the behavior of dismissing the PopupWindow on any outside touch
//        mOverlay.setFocusable(true);

        //Call setOutsideTouchable() if you want to enable outside touches to auto-dismiss
        // the PopupWindow but don't want elements inside the PopupWindow to take focus
//        mOverlay.setOutsideTouchable(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //PopupWindow is like Dialog, it will leak if left visible while the Activity finishes
        mOverlay.dismiss();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Handle any direct touch events passed to the PopupWindow
        return false;
    }

    public void onShowWindowClick(View v) {
        if (mOverlay.isShowing()) {
            mOverlay.dismiss();
        } else {
            //Show the PopupWindow anchored to the button we pressed. It will be displayed
            // below the button if there's room, otherwise above.  PopupWindow must have a
            // fixed size for display above the anchor.  With a WRAP_CONTENT layout mode,
            // the window will simply get measured to the remaining size rather than being
            // shown above in full.
            mOverlay.showAsDropDown(v);
        }
    }

}
