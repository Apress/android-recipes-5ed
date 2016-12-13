package com.androidrecipes.imagemask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class OutlineActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.CENTER);

        //Elevate the view to make a visible shadow
        iv.setElevation(32f);

        iv.setImageResource(R.drawable.dog);

        //Tell the view to use its outline as a clipping mask
        iv.setClipToOutline(true);
        //Provide the circular view outline for clipping and shadows
        iv.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                ImageView iv = (ImageView) view;
                int radius = iv.getDrawable().getIntrinsicHeight() / 2;
                int centerX = (view.getRight() - view.getLeft()) / 2;
                int centerY = (view.getBottom() - view.getTop()) / 2;

                outline.setOval(centerX - radius,
                        centerY - radius,
                        centerX + radius,
                        centerY + radius);
            }
        });

        setContentView(iv);
    }
}
