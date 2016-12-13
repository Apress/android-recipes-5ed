package com.androidrecipes.systemui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ImmersiveStickyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    public void onToggleClick(View v) {
        //Here we only need to hide the UI on a tap because
        // the system will make the controls re-appear for us
        // whe the user does an edge swipe from the top or bottom.
        v.setSystemUiVisibility(
                /* This flag tells Android not to shift 
                 * our layout when resizing the window to
                 * hide/show the system elements
                 */
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                /* This flag hides the system status bar.
                 */
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                /* This flag hides the on-screen controls
                 */
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                /* This flag tells the controls to stay hidden until
                 * the user brings them back explicitly with a gesture,
                 * and hide them again after a period.
                 */
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
